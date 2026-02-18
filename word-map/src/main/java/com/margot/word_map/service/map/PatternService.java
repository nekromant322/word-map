package com.margot.word_map.service.map;

import com.margot.word_map.dto.PatternDto;
import com.margot.word_map.dto.request.CreateUpdatePatternRequest;
import com.margot.word_map.dto.request.PatternSearchRequest;
import com.margot.word_map.dto.response.IdResponse;
import com.margot.word_map.dto.response.PagedResponseDto;
import com.margot.word_map.exception.PageOutOfRangeException;
import com.margot.word_map.exception.PatternNotFoundException;
import com.margot.word_map.mapper.PatternMapper;
import com.margot.word_map.model.Pattern;
import com.margot.word_map.repository.PatternRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PatternService {

    private final PatternRepository patternRepository;
    private final PatternMapper patternMapper;
    private final AuditService auditService;
    private final PatternSpecs patternSpecs;

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

    @Transactional(readOnly = true)
    public PagedResponseDto<PatternDto> getPatternsByFilter(Pageable pageable, PatternSearchRequest request) {
        Specification<Pattern> spec = patternSpecs.fromRequest(request);

        Page<Pattern> page = patternRepository.findAll(spec, pageable);

        if (pageable.getPageNumber() >= page.getTotalPages() && page.getTotalElements() > 0) {
            throw new PageOutOfRangeException();
        }

        Page<PatternDto> dtoPage = page.map(patternMapper::toDto);

        return PagedResponseDto.fromPage(dtoPage);
    }

    public Pattern getByIdOrThrow(Long id) {
        return patternRepository.findById(id)
                .orElseThrow(() -> new PatternNotFoundException("Паттерн не найден по идентификатору: " + id));
    }
}
