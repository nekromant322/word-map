package com.margot.word_map.service.admin;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.AdminInfoDto;
import com.margot.word_map.dto.request.ChangeAdminAccessRequest;
import com.margot.word_map.dto.request.CreateAdminRequest;
import com.margot.word_map.dto.request.UpdateAdminRequest;
import com.margot.word_map.dto.response.GetAdminsResponse;
import com.margot.word_map.exception.AdminAlreadyExistsException;
import com.margot.word_map.exception.InvalidRuleException;
import com.margot.word_map.exception.UserNotAccessException;
import com.margot.word_map.exception.UserNotFoundException;
import com.margot.word_map.mapper.AdminMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.service.language.LanguageService;
import com.margot.word_map.service.rule.RuleService;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final RuleService ruleService;
    private final SecurityAdminAccessor adminAccessor;
    private final LanguageService languageService;

    public GetAdminsResponse getAdmins(Integer page, Integer size) {
        Long countAdmins = adminRepository.count();

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<AdminDto> admins = adminRepository.findAll(pageable).map(adminMapper::toDto);

        return GetAdminsResponse.builder()
                .count(countAdmins)
                .page(page)
                .itemsOnPage(size)
                .admins(admins)
                .build();
    }

    @Transactional
    public AdminInfoDto getCurrentAdminInfo() {
        Long id = adminAccessor.getCurrentAdminId();
        Admin admin = getAdminById(id);
        admin.setDateActive(LocalDateTime.now());

        return adminMapper.toInfoDto(admin);
    }

    public AdminDto getAdminDetailedInfoById(Long id) {
        return adminMapper.toDto(getAdminById(id));
    }

    public Admin getAdminById(Long id) {
        return adminRepository.findById(id).orElseThrow(() ->
            new UserNotFoundException("admin with id " + id + " not found")
        );
    }

    public Admin getActiveAdminById(Long id) {
        Admin admin = getAdminById(id);
        if (!admin.isAccessGranted()) {
            throw new UserNotAccessException("account is blocked: " + admin.getEmail());
        }

        return admin;
    }

    public AdminDto getAdminInfoByEmail(String email) {
        return adminMapper.toDto(getAdminByEmail(email));
    }

    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email).orElseThrow(() ->
            new UserNotFoundException("admin with email " + email + " not found")
        );
    }

    public boolean isAdminExistsByEmail(String email) {
        return adminRepository.existsByEmail(email);
    }

    @Transactional
    public void createAdmin(CreateAdminRequest request) {
        if (isAdminExistsByEmail(request.getEmail())) {
            throw new AdminAlreadyExistsException("admin with email " + request.getEmail() + " already exists");
        }

        Admin admin = Admin.builder()
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .role(Admin.ROLE.MODERATOR)
                .rules(getAdminRules(request.getRuleID(), Admin.ROLE.MODERATOR))
                .build();

        adminRepository.save(admin);
    }

    @Transactional
    public void updateAdmin(UpdateAdminRequest request) {
        Admin admin = getAdminById(request.getId());

        admin.setRole(request.getRole());
        admin.setRules(getAdminRules(request.getRuleIds(), request.getRole()));

        adminRepository.save(admin);
    }

    public void updateCurrentAdminLanguage(Long langId) {
        Long adminId = adminAccessor.getCurrentAdminId();
        languageService.updateAdminLanguage(adminId, langId);
    }

    private List<Rule> getAdminRules(List<Long> ruleIds, Admin.ROLE role) {
        if (role == Admin.ROLE.MODERATOR && ruleIds != null) {
            List<Rule> rules = ruleService.getRulesByIds(ruleIds);
            if (rules.size() != ruleIds.size()) {
                throw new InvalidRuleException("wrong rule provided");
            }

            return rules;
        }
        return new ArrayList<>();
    }

    @Transactional
    public void changeAccess(ChangeAdminAccessRequest request) {
        Admin admin = getAdminById(request.getId());

        admin.setAccessGranted(request.getAccess());
        adminRepository.save(admin);
    }
}
