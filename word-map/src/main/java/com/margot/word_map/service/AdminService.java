package com.margot.word_map.service;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.request.AdminManagementRequest;
import com.margot.word_map.dto.response.GetAdminsResponse;
import com.margot.word_map.exception.*;
import com.margot.word_map.mapper.AdminMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
import com.margot.word_map.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Page<AdminDto> admins = adminMapper.toDto(adminRepository.findAll(pageable));

        return GetAdminsResponse.builder()
                .count(countAdmins)
                .page(page)
                .itemsOnPage(size)
                .admins(admins)
                .build();
    }

    public AdminDto getAdminById(Long id) {
        return adminMapper.toDto(adminRepository.findById(id).orElseThrow(() -> {
            log.info("admin with id {} not found", id);
            return new AdminNotFoundException("admin with id " + id + " not found");
        }));
    }

    @Transactional
    public HttpStatus manageAdmin(AdminManagementRequest request) {
        Optional<Admin> adminOp = adminRepository.findByEmail(request.getEmail());

        if (adminOp.isPresent()) {
            updateAdmin(adminOp.get(), request);
            return HttpStatus.OK;
        } else {
            createAdmin(request);
            return HttpStatus.CREATED;
        }
    }

    private void createAdmin(AdminManagementRequest request) {
        List<Rule> adminRules = ruleService.getRules().stream()
                .filter(rule -> request.getNameRules().contains(rule.getName().name()))
                .toList();

        Admin admin = Admin.builder()
                .email(request.getEmail())
                .dateCreation(LocalDateTime.now())
                .role(Admin.ROLE.valueOf(request.getRole()))
                .access(request.getAccess())
                .roles(adminRules)
                .build();

        adminRepository.save(admin);
    }

    private void updateAdmin(Admin admin, AdminManagementRequest request) {
        List<Rule> adminRules = ruleService.getRules().stream()
                .filter(rule -> request.getNameRules().contains(rule.getName().name()))
                .toList();

        admin.setRoles(adminRules);
        admin.setAccess(request.getAccess());
        admin.setRole(Admin.ROLE.valueOf(request.getRole()));

        adminRepository.save(admin);
    }

}
