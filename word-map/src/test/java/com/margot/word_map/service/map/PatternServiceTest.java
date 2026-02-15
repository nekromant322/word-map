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
import com.margot.word_map.model.enums.PatternType;
import com.margot.word_map.repository.PatternRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatternServiceTest {

    @Mock
    private PatternRepository patternRepository;

    @Mock
    private PatternMapper patternMapper;

    @Mock
    private AuditService auditService;

    @Mock
    private PatternSpecs patternSpecs;

    @InjectMocks
    private PatternService patternService;

    @Test
    public void testGetPatternById() {
        Long patternId = 1L;

        Pattern pattern = Pattern.builder()
                .id(patternId)
                .build();

        PatternDto expectedDto = PatternDto.builder()
                .id(1L)
                .build();

        when(patternRepository.findById(patternId)).thenReturn(Optional.of(pattern));
        when(patternMapper.toDto(any(Pattern.class))).thenReturn(expectedDto);

        PatternDto dto = patternService.getPatternById(patternId);

        assertThat(dto).isSameAs(expectedDto);
    }

    @Test
    public void testCreatePatternSavesAndLogs() {
        CreateUpdatePatternRequest request = new CreateUpdatePatternRequest();
        request.setPatternType(PatternType.REGULAR);

        Pattern mapped = Pattern.builder().build();
        Pattern saved = Pattern.builder().id(1L).build();

        when(patternMapper.toEntity(request)).thenReturn(mapped);
        when(patternRepository.save(any(Pattern.class))).thenReturn(saved);

        IdResponse<Long> result = patternService.createPattern(request);

        assertEquals(1L, result.getId());

        verify(patternRepository).save(mapped);
        verify(auditService).log(AuditActionType.PATTERN_CREATED,1L);
        verify(patternMapper).toEntity(request);

        verifyNoMoreInteractions(patternRepository, patternMapper, auditService);
    }

    @Test
    public void testUpdatePatternSavesAndLogs() {
        Long id = 1L;
        CreateUpdatePatternRequest request = new CreateUpdatePatternRequest();
        request.setPatternType(PatternType.REGULAR);

        Pattern found = Pattern.builder().id(1L).build();
        Pattern saved = Pattern.builder().id(1L).build();

        when(patternRepository.findById(id)).thenReturn(Optional.of(found));
        when(patternRepository.save(any(Pattern.class))).thenReturn(saved);

        IdResponse<Long> result = patternService.updatePattern(id, request);

        assertEquals(id, result.getId());

        verify(patternRepository).findById(id);
        verify(patternRepository).save(found);
        verify(patternMapper).updateEntity(found, request);
        verify(auditService).log(eq(AuditActionType.PATTERN_UPDATED), eq(1L));

        verifyNoMoreInteractions(patternRepository, patternMapper, auditService);
    }

    @Test
    public void testDeletePatternDeletesAndLogs() {
        Long id = 1L;
        Pattern pattern = Pattern.builder()
                .id(1L)
                .build();

        when(patternRepository.findById(id)).thenReturn(Optional.of(pattern));

        IdResponse<Long> result = patternService.deletePattern(id);

        verify(patternRepository).delete(eq(pattern));
        verify(auditService).log(eq(AuditActionType.PATTERN_DELETED), eq(1L));

        assertEquals(1L, result.getId());
    }

    @Test
    public void testGetPatternsByFilterReturnsPagedResponse() {
        PatternSearchRequest request = new PatternSearchRequest();

        Pageable pageable = PageRequest.of(0, 2);

        Pattern pattern1 = Pattern.builder().id(1L).build();
        Pattern pattern2 = Pattern.builder().id(2L).build();

        PatternDto patternDto1 = PatternDto.builder().id(1L).build();
        PatternDto patternDto2 = PatternDto.builder().id(2L).build();

        Specification<Pattern> spec = mock(Specification.class);

        Page<Pattern> page = new PageImpl<>(List.of(pattern1, pattern2), pageable, 2);

        when(patternSpecs.fromRequest(request)).thenReturn(spec);
        when(patternRepository.findAll(spec, pageable)).thenReturn(page);
        when(patternMapper.toDto(pattern1)).thenReturn(patternDto1);
        when(patternMapper.toDto(pattern2)).thenReturn(patternDto2);

        PagedResponseDto<PatternDto> response = patternService.getPatternsByFilter(pageable, request);

        assertNotNull(response);
        assertEquals(1, response.getTotalPages());
        assertEquals(2, response.getTotalElements());
        assertEquals(2, response.getContent().size());
        assertEquals(0, response.getPageable().getPageNumber());

        verify(patternSpecs).fromRequest(request);
        verify(patternRepository).findAll(spec, pageable);
        verify(patternMapper, times(2)).toDto(any(Pattern.class));

        verifyNoMoreInteractions(patternRepository, patternMapper, patternSpecs);
    }

    @Test
    public void testGetPatternsByFilterThrowsWhenWrongPageNumber() {
        PatternSearchRequest request = new PatternSearchRequest();
        Pageable pageable = PageRequest.of(1, 10);

        Specification<Pattern> spec = mock(Specification.class);

        Pattern pattern1 = Pattern.builder().id(1L).build();
        Page<Pattern> page = new PageImpl<>(
                List.of(pattern1),
                PageRequest.of(0, 10),
                1);

        when(patternSpecs.fromRequest(request)).thenReturn(spec);
        when(patternRepository.findAll(spec, pageable)).thenReturn(page);

        assertThrows(
                PageOutOfRangeException.class,
                () -> patternService.getPatternsByFilter(pageable, request)
        );

        verify(patternSpecs).fromRequest(request);
        verify(patternRepository).findAll(spec, pageable);

        verifyNoInteractions(patternMapper);
    }

    @Test
    public void testGetByIdThrowsWhenNoPatternFound() {
        Long id = 1L;

        when(patternRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> patternService.getByIdOrThrow(id))
                .isInstanceOf(PatternNotFoundException.class)
                .hasMessageContaining("Паттерн не найден по идентификатору: 1");

    }
}
