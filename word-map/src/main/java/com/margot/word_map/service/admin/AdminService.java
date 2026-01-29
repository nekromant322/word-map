package com.margot.word_map.service.admin;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.AdminInfoDto;
import com.margot.word_map.dto.AdminListQueryDto;
import com.margot.word_map.dto.request.AdminSearchRequest;
import com.margot.word_map.dto.request.ChangeAdminAccessRequest;
import com.margot.word_map.dto.request.CreateAdminRequest;
import com.margot.word_map.dto.request.UpdateAdminRequest;
import com.margot.word_map.dto.response.PagedResponseDto;
import com.margot.word_map.exception.*;
import com.margot.word_map.mapper.AdminMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
import com.margot.word_map.model.enums.Role;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.specification.AdminSpecification;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.service.language.LanguageService;
import com.margot.word_map.service.rule.RuleService;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.margot.word_map.service.audit.AuditActionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final RuleService ruleService;
    private final SecurityAdminAccessor adminAccessor;
    private final LanguageService languageService;
    private final AdminSpecification adminSpecs;
    private final AuditService auditService;

    public PagedResponseDto<AdminListQueryDto> getAdmins(Pageable pageable, AdminSearchRequest request) {

        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Specification<Admin> spec = Specification
                .where(adminSpecs.hasName(request.getSearch()))
                .and(adminSpecs.hasRole(request.getFilterRole()))
                .and(adminSpecs.hasLanguage(request.getFilterLanguage()))
                .and(adminSpecs.hasAccess(request.getFilterAccess()))
                .and(adminSpecs.orderBy(request.getSortingType(), true));

        Page<AdminListQueryDto> page = adminRepository.findAll(spec, pageRequest)
                .map(adminMapper::toListQueryDto);

        if (pageable.getPageNumber() >= page.getTotalPages() && page.getTotalElements() > 0) {
            throw new PageOutOfRangeException("requested page is out of range, total pages " + page.getTotalPages());
        }

        return PagedResponseDto.fromPage(page);
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
            new AdminNotFoundException("admin with id " + id + " not found")
        );
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
                .role(Role.MODERATOR)
                .rules(getAdminRules(request.getRuleID()))
                .build();

        adminRepository.save(admin);
        auditService.log(ADMIN_CREATED, admin.getEmail());
    }

    @Transactional
    public void updateAdmin(Long id, UpdateAdminRequest request) {
        Admin targetAdmin = getAdminById(id);

        if (targetAdmin.getRole() == Role.ADMIN) {
            throw new UserNotPermissionsException("can't set rules for admin role");
        }

        targetAdmin.getRules().clear();
        targetAdmin.getRules().addAll(getAdminRules(request.getRuleId()));

        adminRepository.save(targetAdmin);
        auditService.log(ADMIN_UPDATED, targetAdmin.getEmail());
    }

    public void updateCurrentAdminLanguage(Long langId) {
        Long adminId = adminAccessor.getCurrentAdminId();
        languageService.updateAdminLanguage(adminId, langId);
    }

    @Transactional
    public void changeAccess(Long id, ChangeAdminAccessRequest request) {
        Admin targetAdmin = getAdminById(id);

        if (targetAdmin.getRole() == Role.ADMIN) {
            throw new UserNotPermissionsException("can't change access for user with admin role");
        }

        targetAdmin.setAccessGranted(request.getAccess());
        adminRepository.save(targetAdmin);

        auditService.log(ADMIN_ACCESS_CHANGED, targetAdmin.getEmail());
    }
    
    private Set<Rule> getAdminRules(List<Long> ruleIds) {
        Set<Rule> rules = ruleService.getRulesByIds(ruleIds);

        if (rules.size() != ruleIds.size()) {
            throw new InvalidRuleException("wrong rule provided");
        }

        return rules;
    }
}
