package com.margot.word_map.service.language;

import com.margot.word_map.dto.LanguageDto;
import com.margot.word_map.exception.LanguageNotFoundException;
import com.margot.word_map.mapper.LanguageMapper;
import com.margot.word_map.model.Language;
import com.margot.word_map.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;

    private final LanguageMapper languageMapper;

    public List<LanguageDto> getLanguages() {
        return languageRepository.findAll().stream().map(languageMapper::toDto).toList();
    }

    public LanguageDto getLanguageByPrefix(String prefix) {
        return languageMapper.toDto(languageRepository.getLanguageByPrefix(prefix).orElseThrow(() -> {
            log.info("language with prefix {} not found", prefix);
            return new LanguageNotFoundException("language with prefix " + prefix + " not found");
        }));
    }

    public LanguageDto getLanguageById(Long id) {
        return languageMapper.toDto(languageRepository.findById(id).orElseThrow(() -> {
            log.info("language with id {} not found", id);
            return new LanguageNotFoundException("language with id " + id + " not found");
        }));
    }

    public Optional<Language> findById(Long id) {
        return languageRepository.findById(id);
    }

    public void createLanguage() {

    }

    public void updateLanguage() {
        
    }

    public void deleteLanguage() {

    }
}
