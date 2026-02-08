package com.margot.word_map.service.language;

import com.margot.word_map.dto.LanguageDto;
import com.margot.word_map.dto.OptionDto;
import com.margot.word_map.dto.request.CreateUpdateLanguageRequest;
import com.margot.word_map.exception.DuplicateNameException;
import com.margot.word_map.exception.DuplicatePrefixException;
import com.margot.word_map.exception.LanguageNotFoundException;
import com.margot.word_map.mapper.LanguageMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.AdminLanguage;
import com.margot.word_map.model.Language;
import com.margot.word_map.repository.AdminLanguageRepository;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.LanguageRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final AdminLanguageRepository adminLanguageRepository;
    private final AdminRepository adminRepository;
    private final AuditService auditService;

    private final LanguageMapper languageMapper;

    @Transactional(readOnly = true)
    public List<LanguageDto> getLanguages() {
        return languageRepository.findAll().stream()
                .map(languageMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OptionDto> getLanguageOptions() {
        return languageRepository.findAll().stream()
                .map(languageMapper::toOptionDto)
                .toList();
    }

    public Optional<Language> findByName(String name) {
        return languageRepository.findByName(name);
    }

    public LanguageDto getLanguageByPrefix(String prefix) {
        return languageMapper.toDto(languageRepository.getLanguageByPrefix(prefix).orElseThrow(() -> {
            log.info("language with prefix {} not found", prefix);
            return new LanguageNotFoundException("language with prefix " + prefix + " not found");
        }));
    }

    public Language getLanguageById(Long id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new LanguageNotFoundException("language with id " + id + " not found"));
    }

    public Optional<Language> findById(Long id) {
        return languageRepository.findById(id);
    }

    @Transactional
    public void updateAdminLanguage(Long adminId, Long langId) {
        Language language = getLanguageById(langId);

        AdminLanguage adminLanguage = adminLanguageRepository.findByAdminIdAndLanguageId(adminId, langId)
                .orElseGet(() -> createAdminLanguage(adminId, language));

        adminLanguage.setLastUsedAt(LocalDateTime.now());
        adminLanguageRepository.save(adminLanguage);
    }

    private AdminLanguage createAdminLanguage(Long adminId, Language language) {
        Admin adminRef = adminRepository.getReferenceById(adminId);

        return AdminLanguage.builder()
                .admin(adminRef)
                .language(language)
                .build();
    }

    @Transactional
    public LanguageDto createLanguage(CreateUpdateLanguageRequest request) {
        validateByFields(request, null);

        Language language = Language.builder()
                .name(request.getName())
                .prefix(request.getPrefix())
                .build();

        languageRepository.save(language);
        auditService.log(AuditActionType.LANGUAGE_CREATED, request.getName());

        return languageMapper.toDto(language);
    }

    @Transactional
    public LanguageDto updateLanguage(Long langId, CreateUpdateLanguageRequest request) {
        validateByFields(request, langId);

        Language language = languageRepository.findById(langId)
                .orElseThrow(LanguageNotFoundException::new);

        language.setPrefix(request.getPrefix());
        language.setName(request.getName());

        languageRepository.save(language);
        auditService.log(AuditActionType.LANGUAGE_UPDATED, request.getName());

        return languageMapper.toDto(language);
    }

    public void deleteLanguage() {

    }

    public void validateByFields(CreateUpdateLanguageRequest request, Long excludeId) {
        if (languageRepository.existsByPrefixExcludeId(request.getPrefix(), excludeId)) {
            throw new DuplicatePrefixException("Язык с данным префиксом уже существует: " + request.getPrefix());
        }

        if (languageRepository.existsByNameExcludeId(request.getName(), excludeId)) {
            throw new DuplicateNameException("Язык с данным именем уже существует: " + request.getName());
        }
    }
}
