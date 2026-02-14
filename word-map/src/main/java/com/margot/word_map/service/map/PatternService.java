package com.margot.word_map.service.map;

import com.margot.word_map.dto.PatternDto;
import com.margot.word_map.exception.PatternNotFoundException;
import com.margot.word_map.mapper.PatternMapper;
import com.margot.word_map.repository.PatternRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatternService {

    private final PatternRepository patternRepository;
    private final PatternMapper patternMapper;

    @Transactional(readOnly = true)
    public PatternDto getPatternById(Long id) {
        return patternRepository.findById(id)
                .map(patternMapper::toDto)
                .orElseThrow(() -> new PatternNotFoundException("Паттерн не найден по идентификатору: " + id));
    }
}
