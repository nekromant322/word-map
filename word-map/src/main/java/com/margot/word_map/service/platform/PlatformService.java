package com.margot.word_map.service.platform;

import com.margot.word_map.dto.PlatformDto;
import com.margot.word_map.dto.request.CreatePlatformRequest;
import com.margot.word_map.exception.DuplicateNameException;
import com.margot.word_map.mapper.PlatformMapper;
import com.margot.word_map.model.Platform;
import com.margot.word_map.repository.PlatformRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlatformService {

    private final PlatformRepository platformRepository;
    private final AuditService auditService;
    private final PlatformMapper platformMapper;

    @Transactional
    public PlatformDto createPlatform(CreatePlatformRequest request) {
        if (platformRepository.existsByName(request.getName())) {
            throw new DuplicateNameException("Платформа с таким названием уже существует: " + request.getName());
        }

        Platform platform = new Platform();
        platform.setName(request.getName());

        platformRepository.save(platform);
        auditService.log(AuditActionType.PLATFORM_CREATED, request.getName());

        return platformMapper.toDto(platform);
    }
}
