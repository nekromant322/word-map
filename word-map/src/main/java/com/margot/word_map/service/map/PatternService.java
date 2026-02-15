package com.margot.word_map.service.map;

import com.margot.word_map.dto.PatternDto;
import com.margot.word_map.dto.request.CreateUpdatePatternRequest;
import com.margot.word_map.dto.response.IdResponse;
import com.margot.word_map.exception.PatternNotFoundException;
import com.margot.word_map.mapper.PatternMapper;
import com.margot.word_map.model.Pattern;
import com.margot.word_map.repository.PatternRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatternService {

    private final PatternRepository patternRepository;
    private final PatternMapper patternMapper;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public PatternDto getPatternById(Long id) {
        return patternRepository.findById(id)
                .map(patternMapper::toDto)
                .orElseThrow(() -> new PatternNotFoundException("Паттерн не найден по идентификатору: " + id));
    }

    @Transactional
    public IdResponse<Long> createPattern(CreateUpdatePatternRequest request) {
        Pattern pattern = patternMapper.toEntity(request);

        Pattern saved = patternRepository.save(pattern);
        auditService.log(AuditActionType.PATTERN_CREATED, saved.getId());

        return new IdResponse<>(saved.getId());
    }

    @Transactional
    public IdResponse<Long> updatePattern(Long id, CreateUpdatePatternRequest request) {
        Pattern pattern = patternRepository.findById(id)
                .orElseThrow(() -> new PatternNotFoundException("Паттерн не найден по идентификатору: " + id));

        patternMapper.updateEntity(pattern, request);

        Pattern saved = patternRepository.save(pattern);
        auditService.log(AuditActionType.PATTERN_UPDATED, saved.getId());

        return new IdResponse<>(saved.getId());
    }
}
