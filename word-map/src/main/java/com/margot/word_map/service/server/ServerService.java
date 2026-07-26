package com.margot.word_map.service.server;

import com.margot.word_map.dto.request.CreateServerRequest;
import com.margot.word_map.dto.request.ListServerRequest;
import com.margot.word_map.dto.request.ServerSortingType;
import com.margot.word_map.dto.request.UpdateServerRequest;
import com.margot.word_map.dto.response.DeleteServerResponse;
import com.margot.word_map.dto.response.ListServerResponse;
import com.margot.word_map.exception.*;
import com.margot.word_map.mapper.ServerMapper;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.Platform;
import com.margot.word_map.model.Server;
import com.margot.word_map.repository.ServerRepository;
import com.margot.word_map.repository.specification.ServerSpecification;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.service.language.LanguageService;
import com.margot.word_map.service.map.GridService;
import com.margot.word_map.service.platform.PlatformService;
import com.margot.word_map.service.player.PlayerService;
import com.margot.word_map.service.word.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ServerService {
    private final PlatformService platformService;

    private final LanguageService languageService;

    private final ServerRepository serverRepository;

    private final AuditService auditService;

    private final GridService gridService;

    private final PlayerService playerService;

    private final WordService wordService;

    private final ServerLockService serverLockService;

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private final ServerSpecification serverSpecification;
    private final ServerMapper serverMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createServer(CreateServerRequest request) {
        Platform platform = platformService.findById(request.getPlatform()).orElseThrow(()
                -> new PlatformNotFoundException("Платформа не найдена"));

        Language language = languageService.findById(request.getLanguage()).orElseThrow(()
                -> new LanguageNotFoundException("Нет языка с таким id"));

        if (serverRepository
                .existsByPlatformIdAndLanguageIdAndIsOpenTrue(request.getPlatform(), request.getLanguage())) {
            throw new DuplicateServerException("Сервер уже существует");
        }

        Server server = Server
                .builder()
                .platform(platform)
                .language(language)
                .name(request.getName())
                .wipeCount(0)
                .isOpen(true)
                .build();
        serverRepository.save(server);

        /* ToDo: Проработка алгоритма генерации игрового мира.
        https://override-platform.atlassian.net/wiki/spaces/W/pages/500826193/POST+server
         */
        auditService.log(AuditActionType.SERVER_CREATED, server.getId());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateServerName(UpdateServerRequest request, Long serverId) {
        Server serverToUpdate = serverRepository.findById(serverId).orElseThrow(()
                -> new ServerNotFoundException("Сервер не найден"));
        serverToUpdate.setName(request.getName());
        serverRepository.save(serverToUpdate);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void closeServer(Long serverId) {
        serverLockService.startWipe(serverId);
        serverRepository.findById(serverId).orElseThrow(()
                -> new ServerNotFoundException("Сервер не найден"));
        try {
            gridService.deleteByServerId(serverId);
            wordService.deleteByServerId(serverId);
            playerService.deleteByServerId(serverId);
            auditService.log(AuditActionType.SERVER_CLOSED, serverId);
        } finally {
            serverLockService.completeClosing(serverId, LocalDateTime.now(), false);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void wipeServer(Long serverId) {
        serverLockService.startWipe(serverId);
        Server serverToWipe = serverRepository.findById(serverId).orElseThrow(()
                -> new ServerNotFoundException("Сервер не найден"));
        try {

            gridService.deleteByServerId(serverId);
            /* ToDo: Проработка алгоритма генерации игрового мира.
            https://override-platform.atlassian.net/wiki/spaces/W/pages/500826213/DELETE+server+id+wipe
             */
            auditService.log(AuditActionType.SERVER_WIPED, serverId);
        } finally {
            serverLockService.completeWipe(serverId, LocalDateTime.now(), serverToWipe.getWipeCount() + 1);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public DeleteServerResponse deleteServer(Long serverId) {
        Server serverToDelete = serverRepository.findById(serverId).orElseThrow(()
                -> new ServerNotFoundException("Сервер не найден"));
        String languageName = serverToDelete.getLanguage().getName();
        String platformName = serverToDelete.getPlatform().getName();
        if (serverToDelete.getCleanupInProgress() && serverToDelete.getIsOpen()) {
            throw new ServerCleanupInProgressException("Выполняется очистка");
        }
        if (serverToDelete.getIsOpen()) {
            throw new InvalidConditionException();
        }
        serverRepository.deleteById(serverId);
        auditService.log(AuditActionType.SERVER_DELETED, serverId, languageName, platformName);
        return DeleteServerResponse.builder().
                id(serverToDelete.getId())
                .language(languageName)
                .platform(platformName)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ListServerResponse getServers(Integer page, Integer size, ListServerRequest request) {
        Specification<Server> specification = serverSpecification.filter(request.getFilterStatus(),
                request.getFilterPlatform(), request.getFilterLanguage(), request.getSearch());
        long totalElements = serverRepository.count(specification);
        int pageSize = (size != null && size > 0) ? size : DEFAULT_PAGE_SIZE;
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        int numberPage;
        if (page != null && page > 0) {
            if (totalPages > 0) {
                numberPage = Math.min(page, totalPages);
            } else {
                numberPage = DEFAULT_PAGE_NUMBER;
            }
        } else {
            numberPage = DEFAULT_PAGE_NUMBER;
        }
        Sort sort = request.getSortingType() != null
                ? request.getSortingType().toSort() : ServerSortingType.DATE_LATE.toSort();
        Pageable pageable = PageRequest.of(numberPage - 1, pageSize, sort);
        Page<Server> serverPage = serverRepository.findAll(specification, pageable);
        return serverMapper.toResponse(serverPage.getContent(), numberPage,
                pageSize, totalPages, totalElements);
    }
}
