package com.margot.word_map.service.server;

import com.margot.word_map.dto.request.CreateServerRequest;
import com.margot.word_map.exception.DuplicateServerException;
import com.margot.word_map.exception.LanguageNotFoundException;
import com.margot.word_map.exception.PlatformNotFoundException;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.Platform;
import com.margot.word_map.model.Server;
import com.margot.word_map.repository.ServerRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.service.language.LanguageService;
import com.margot.word_map.service.platform.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ServerService {
    private final PlatformService platformService;

    private final LanguageService languageService;

    private final ServerRepository serverRepository;

    private final AuditService auditService;

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

        // ToDo: Проработка алгоритма генерации игрового мира.
        auditService.log(AuditActionType.SERVER_CREATED, server.getId());
    }
}
