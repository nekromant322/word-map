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
        return patternMapper.toDto(getByIdOrThrow(id));
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
        Pattern pattern = getByIdOrThrow(id);

        patternMapper.updateEntity(pattern, request);

        Pattern saved = patternRepository.save(pattern);
        auditService.log(AuditActionType.PATTERN_UPDATED, saved.getId());

        return new IdResponse<>(saved.getId());
    }

    @Transactional
    public IdResponse<Long> deletePattern(Long id) {
        Pattern pattern = getByIdOrThrow(id);

        IdResponse<Long> result = new IdResponse<>(pattern.getId());

        patternRepository.delete(pattern);
        auditService.log(AuditActionType.PATTERN_DELETED, result.getId());

        return result;
    }

    public Pattern getByIdOrThrow(Long id) {
        return patternRepository.findById(id)
                .orElseThrow(() -> new PatternNotFoundException("Паттерн не найден по идентификатору: " + id));
    }
}
