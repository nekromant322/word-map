package com.margot.word_map.service.admin;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.request.ChangeAdminAccessRequest;
import com.margot.word_map.dto.request.CreateAdminRequest;
import com.margot.word_map.dto.request.UpdateAdminRequest;
import com.margot.word_map.dto.response.GetAdminsResponse;
import com.margot.word_map.exception.*;
import com.margot.word_map.mapper.AdminMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.service.rule.RuleService;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final AdminMapper adminMapper;

    private final RuleService ruleService;

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

    public AdminDto getAdminInfoById(Long id) {
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
            log.info("admin with email {} already exists", request.getEmail());
            throw new AdminAlreadyExistsException("admin with email " + request.getEmail() + " already exists");
        }

        Admin admin = Admin.builder()
                .email(request.getEmail())
                .createdAt(LocalDateTime.now())
                .role(Admin.ROLE.valueOf(request.getRole()))
                .rules(getAdminRules(request.getNameRules(), request.getRole()))
                .accessGranted(request.getAccess())
                .build();

        adminRepository.save(admin);
    }

    @Transactional
    public void updateAdmin(UpdateAdminRequest request) {
        Admin admin = getAdminById(request.getId());

        admin.setRole(Admin.ROLE.valueOf(request.getRole()));
        admin.setRules(getAdminRules(request.getNameRules(), request.getRole()));

        adminRepository.save(admin);
    }

    private List<Rule> getAdminRules(List<String> needRules, String role) {
        if (role.equals(Admin.ROLE.MODERATOR.name()) && needRules != null) {
            return ruleService.getRules().stream()
                    .filter(rule -> needRules.contains(rule.getName().name()))
                    .collect(Collectors.toList());
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
