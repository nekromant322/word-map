package com.margot.word_map.service;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.AdminType;
import com.margot.word_map.dto.request.AdminRoleRequest;
import com.margot.word_map.dto.request.ChangeAdminTypeRequest;
import com.margot.word_map.dto.request.CreateAdminRequest;
import com.margot.word_map.dto.response.GetAdminsResponse;
import com.margot.word_map.exception.*;
import com.margot.word_map.mapper.AdminMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Role;
import com.margot.word_map.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;

    private final AdminMapper adminMapper;

    private final RoleService roleService;

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

    public void createAdmin(CreateAdminRequest request) {
        if (adminRepository.existsByEmail(request.getEmail())) {
            log.info("admin with email {} already exists", request.getEmail());
            throw new AdminAlreadyExistsException("admin with email " + request.getEmail() + " already exists");
        }

        List<Role> adminRoles = resolveAdminRoles(request.getRoles(), request.getAdminType());

        Admin admin = Admin.builder()
                .email(request.getEmail())
                .dateCreation(LocalDateTime.now())
                .access(true)
                .roles(adminRoles)
                .build();
        adminRepository.save(admin);
    }

    private List<Role> resolveAdminRoles(List<Role.ROLE> rolesToAdmin, AdminType adminType) {
        List<Role> roles = roleService.getRoles();

        List<Role> adminRoles = new LinkedList<>();

        if (adminType.equals(AdminType.ADMIN)) {
            adminRoles.add(roles.stream()
                    .filter(x -> x.getRole().equals(Role.ROLE.ADMIN))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.warn("role {} not found", Role.ROLE.ADMIN);
                        return new RoleNotFoundException("role " + Role.ROLE.ADMIN + " not found");
                    })
            );
        } else {
            List<Role> availableRoles = roles.stream()
                    .filter(x -> x.getLevel().equals(Role.LEVEL.AVAILABLE))
                    .toList();
            List<Role> settingRoles = roles.stream()
                    .filter(x -> x.getLevel().equals(Role.LEVEL.SETTING))
                    .filter(x -> rolesToAdmin.contains(x.getRole()))
                    .toList();

            adminRoles.addAll(availableRoles);
            adminRoles.addAll(settingRoles);
        }
        return adminRoles;
    }

    public void addAdminRole(AdminRoleRequest request) {
        Admin admin = adminRepository.findById(request.getAdminId()).orElseThrow(() -> {
            log.info("admin with id {} not found", request.getAdminId());
            return new AdminNotFoundException("admin with id " + request.getAdminId() + " not found");
        });

        // проверка на то, что админ
        if (admin.getRoles().stream()
                .anyMatch(role -> role.getRole() == Role.ROLE.ADMIN)) {
            return;
        }

        if (admin.getRoles().stream().anyMatch(role -> role.getRole() == request.getRole())) {
            return;
        };

        Role roleToAdd = roleService.getRoleByRole(request.getRole()).orElseThrow(() -> {
            log.info("role {} not found", request.getRole().name());
            return new RoleNotFoundException("role " + request.getRole().name() + " not found");
        });

        if (!roleToAdd.getLevel().equals(Role.LEVEL.SETTING)) {
            log.info("try to add setting role");
            throw new NotRightRoleLevelException("try to add setting role");
        }

        admin.getRoles().add(roleToAdd);
        adminRepository.save(admin);
    }

    public void deleteAdminRole(AdminRoleRequest request) {
        Admin admin = adminRepository.findById(request.getAdminId()).orElseThrow(() -> {
            log.info("admin with id {} not found", request.getAdminId());
            return new AdminNotFoundException("admin with id " + request.getAdminId() + " not found");
        });

        List<Role> adminRoles = admin.getRoles();
        Role requestRole = adminRoles.stream()
                .filter(x -> x.getRole().equals(request.getRole()))
                .findFirst().orElseThrow(() -> {
                    // нет такой роли у админа
                    log.info("admin {} not have role {}", admin.getEmail(), request.getRole().name());
                    return new RoleNotBelongToAdminException("admin not have role " + request.getRole());
                });

        // удалять можно только роли уровня SETTING
        if (!requestRole.getLevel().equals(Role.LEVEL.SETTING)) {
            log.info("try to delete not setting role");
            throw new NotRightRoleLevelException("try to delete not setting role");
        }

        adminRoles.remove(requestRole);
        adminRepository.save(admin);
    }

    public void changeAdminType(ChangeAdminTypeRequest request) {
        Admin admin = adminRepository.findById(request.getAdminId()).orElseThrow(() -> {
            log.info("admin with id {} not found", request.getAdminId());
            return new AdminNotFoundException("admin with id " + request.getAdminId() + " not found");
        });

        if (request.getType() == AdminType.ADMIN &&
                admin.getRoles().stream().noneMatch(role -> role.getRole() == Role.ROLE.ADMIN)
        ) {
            admin.setRoles(new ArrayList<>(Collections.singletonList(
                    roleService.getRoleByRole(Role.ROLE.ADMIN).orElseThrow(() -> {
                        log.warn("ROLE ADMIN not found");
                        return new RoleNotFoundException("ROLE ADMIN not found");
                    })
            )));
        } else if (request.getType() == AdminType.MODERATOR &&
                admin.getRoles().stream().anyMatch(role -> role.getRole() == Role.ROLE.ADMIN)) {
            admin.setRoles(roleService.getRolesByLevel(Role.LEVEL.AVAILABLE));
        } else {
            log.info("adminType and roles matched");
            throw new MismatchAdminTypeException("adminType and roles matched");
        }

        adminRepository.save(admin);
    }
}
