package com.margot.word_map.service;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.request.CreateAdminRequest;
import com.margot.word_map.dto.request.DeleteAdminRoleRequest;
import com.margot.word_map.dto.response.GetAdminsResponse;
import com.margot.word_map.exception.AdminAlreadyExistsException;
import com.margot.word_map.exception.AdminNotFoundException;
import com.margot.word_map.exception.NotRightRoleLevelException;
import com.margot.word_map.exception.RoleNotBelongToAdminException;
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

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

        List<Role> roles = roleService.getRoles();

        Set<Role> adminRoles = new HashSet<>();

        if (request.getAdminType().equals(CreateAdminRequest.AdminType.ADMIN)) {
            adminRoles.add(roles.stream().filter(x -> x.getRole().equals(Role.ROLE.ADMIN)).findFirst().get());
        } else {
            List<Role> availableRoles = roles.stream()
                    .filter(x -> x.getLevel().equals(Role.LEVEL.AVAILABLE))
                    .toList();
            List<Role> settingRoles = roles.stream()
                    .filter(x -> x.getLevel().equals(Role.LEVEL.SETTING))
                    .filter(x -> request.getRoles().contains(x.getRole()))
                    .toList();

            adminRoles.addAll(availableRoles);
            adminRoles.addAll(settingRoles);
        }

        Admin admin = Admin.builder()
                .email(request.getEmail())
                .dateCreation(LocalDateTime.now())
                .access(true)
                .roles(adminRoles)
                .build();
        adminRepository.save(admin);
    }

    public void deleteAdminRole(DeleteAdminRoleRequest request) {
        Admin admin = adminRepository.findById(request.getAdminId()).orElseThrow(() -> {
            log.info("admin with id {} not found", request.getAdminId());
            return new AdminNotFoundException("admin with id " + request.getAdminId() + " not found");
        });

        List<Role> adminRoles = new java.util.ArrayList<>(admin.getRoles().stream().toList());
        Role requestRole = adminRoles.stream().filter(x -> x.getRole().equals(request.getRole())).findFirst().orElseThrow(() -> {
            // нет такой роли у админа
            log.info("admin {} not have role {}", admin.getEmail(), request.getRole().name());
            return new RoleNotBelongToAdminException("admin not have role " + request.getRole());
        });

        if (!requestRole.getLevel().equals(Role.LEVEL.SETTING)) {
            log.info("try to delete not setting role");
            throw new NotRightRoleLevelException("try to delete not setting role");
        }

        adminRoles.remove(requestRole);
        admin.setRoles(new HashSet<>(adminRoles));
        adminRepository.save(admin);
    }
}
