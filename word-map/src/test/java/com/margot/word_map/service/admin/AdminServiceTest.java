package com.margot.word_map.service.admin;

import com.margot.word_map.dto.AdminInfoDto;
import com.margot.word_map.dto.AdminListQueryDto;
import com.margot.word_map.dto.request.AdminSearchRequest;
import com.margot.word_map.dto.request.ChangeAdminAccessRequest;
import com.margot.word_map.dto.request.CreateAdminRequest;
import com.margot.word_map.dto.request.UpdateAdminRequest;
import com.margot.word_map.dto.response.PagedResponseDto;
import com.margot.word_map.exception.AdminAlreadyExistsException;
import com.margot.word_map.exception.AdminNotFoundException;
import com.margot.word_map.exception.PageOutOfRangeException;
import com.margot.word_map.exception.UserNotPermissionsException;
import com.margot.word_map.mapper.AdminMapper;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
import com.margot.word_map.model.enums.Role;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.specification.AdminSpecification;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.service.rule.RuleService;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private SecurityAdminAccessor adminAccessor;

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private RuleService ruleService;

    @Mock
    private AdminSpecification adminSpecs;

    @InjectMocks
    private AdminService adminService;

    @Test
    public void testGetAdminsReturnsPagedResponse() {
        Admin admin = getTestAdmin();
        Pageable pageable = PageRequest.of(1, 10);
        AdminSearchRequest request = new AdminSearchRequest();

        Page<Admin> adminPage = new PageImpl<>(List.of(admin), pageable, 1);
        AdminListQueryDto dto = new AdminListQueryDto();

        when(adminRepository.findAll(
                ArgumentMatchers.<Specification<Admin>>any(),
                any(Pageable.class)
        )).thenReturn(adminPage);
        when(adminMapper.toListQueryDto(admin)).thenReturn(dto);

        PagedResponseDto<AdminListQueryDto> result = adminService.getAdmins(pageable, request);

        assertThat(result.getContent())
                .containsExactly(dto);
    }

    @Test
    public void testGetAdminsThrowsWhenWrongPageNumber() {
        Admin admin = getTestAdmin();
        Pageable pageable = PageRequest.of(2, 10);
        AdminSearchRequest request = new AdminSearchRequest();

        Page<Admin> adminPage = new PageImpl<>(List.of(admin), PageRequest.of(1, 1), 1);

        when(adminRepository.findAll(
                ArgumentMatchers.<Specification<Admin>>any(),
                any(Pageable.class)
        )).thenReturn(adminPage);

        assertThatThrownBy(() -> adminService.getAdmins(pageable, request))
                .isInstanceOf(PageOutOfRangeException.class)
                .hasMessageContaining(
                        "Запрошенная страница выходит за пределы диапазона. Всего страниц: " + adminPage.getTotalPages());
    }

    @Test
    public void testGetCurrentAdminInfoReturnsCorrectInfo()  {
        Long id = 1L;
        String email = "admin@example.com";
        Role role = Role.ADMIN;

        Admin admin = Admin.builder()
                .id(id)
                .email(email)
                .role(role)
                .build();

        AdminInfoDto expectedDto = AdminInfoDto.builder()
                .email(email)
                .role(role.name().toLowerCase())
                .build();

        when(adminAccessor.getCurrentAdmin()).thenReturn(admin);
        when(adminMapper.toInfoDto(admin)).thenReturn(expectedDto);

        AdminInfoDto result = adminService.getCurrentAdminInfo();

        assertThat(result).isSameAs(expectedDto);
        assertThat(admin.getDateActive()).isNotNull();

        verify(adminAccessor).getCurrentAdmin();
        verify(adminMapper).toInfoDto(admin);
    }

    @Test
    public void testGetAdminByIdReturnsCorrectAdmin() {
        Long id = 1L;
        Admin admin = getTestAdmin();

        when(adminRepository.findById(id)).thenReturn(Optional.of(admin));

        Admin result = adminService.getAdminById(id);

        assertThat(result).isSameAs(admin);
        verify(adminRepository).findById(id);
    }

    @Test
    public void testGetAdminByIdThrowsWhenNoAdmin() {
        Long id = 1L;
        when(adminRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getAdminById(id))
                .isInstanceOf(AdminNotFoundException.class)
                .hasMessageContaining("Администратор с id ");

        verify(adminRepository).findById(id);
    }

    @Test
    public void testCreateAdminSavesAndLogs() {
        List<Long> ruleIds = List.of(1L);
        CreateAdminRequest request = CreateAdminRequest.builder()
                .email("test@example.com")
                .ruleID(ruleIds)
                .build();
        Rule manageDictionary = new Rule(1L, Rule.RULE.MANAGE_DICTIONARY);

        when(ruleService.getRulesByIds(request.getRuleID())).thenReturn(Set.of(manageDictionary));

        adminService.createAdmin(request);

        ArgumentCaptor<Admin> adminCaptor = ArgumentCaptor.forClass(Admin.class);
        verify(adminRepository).save(adminCaptor.capture());

        Admin admin = adminCaptor.getValue();

        assertThat(admin.getRole()).isEqualTo(Role.MODERATOR);
        assertThat(admin.getRules()).containsExactly(manageDictionary);
        assertThat(admin.getCreatedAt()).isNotNull();
        assertThat(admin.getEmail()).isEqualTo(request.getEmail());
        verify(auditService).log(eq(AuditActionType.ADMIN_CREATED), eq(admin.getEmail()));
    }

    @Test
    public void testCreateAdminThrowsWhenAdminExists() {
        CreateAdminRequest request = CreateAdminRequest.builder()
                .email("test@example.com")
                .build();

        when(adminRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> adminService.createAdmin(request))
                .isInstanceOf(AdminAlreadyExistsException.class)

                .hasMessageContaining(String.format("Администратор с электронной почтой %s уже существует", request.getEmail()));

        verify(adminRepository, times(0)).save(any());
    }

    @Test
    public void testUpdateAdminUpdatesAndLogs() {
        Rule manageDictionary = new Rule(1L, Rule.RULE.MANAGE_DICTIONARY);
        Rule manageRating = new Rule(2L, Rule.RULE.MANAGE_RATING);

        Long adminId = 1L;
        Admin admin = Admin.builder()
                .id(adminId)
                .role(Role.MODERATOR)
                .email("test@example.com")
                .build();
        admin.getRules().add(manageDictionary);

        List<Long> ruleIds = List.of(2L);
        UpdateAdminRequest request = UpdateAdminRequest.builder()
                .ruleId(ruleIds)
                .build();

        when(ruleService.getRulesByIds(ruleIds)).thenReturn(Set.of(manageRating));
        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        adminService.updateAdmin(adminId, request);

        verify(auditService).log(eq(AuditActionType.ADMIN_UPDATED), eq(admin.getEmail()));

        assertThat(admin.getRules())
                .containsExactly(manageRating);
    }

    @Test
    public void testUpdateAdminThrowsWhenRoleIsAdmin() {
        Long adminId = 1L;
        Admin admin = getTestAdmin();

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> adminService.updateAdmin(adminId, new UpdateAdminRequest()))
                .isInstanceOf(UserNotPermissionsException.class)
                .hasMessageContaining("Нельзя изменять права для роли администратора");
    }

    @Test
    public void testChangeAccessChangesAndLogs() {
        Long adminId = 1L;
        Admin admin = Admin.builder()
                .id(adminId)
                .email("test@example.com")
                .role(Role.MODERATOR)
                .accessGranted(true)
                .build();

        ChangeAdminAccessRequest request = new ChangeAdminAccessRequest(false);

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        adminService.changeAccess(adminId, request);

        verify(auditService).log(eq(AuditActionType.ADMIN_ACCESS_CHANGED), eq(admin.getEmail()));
        assertThat(admin.isAccessGranted()).isFalse();
    }

    @Test
    public void testChangeAccessThrowsWhenRoleIsAdmin() {
        Long adminId = 1L;
        Admin admin = getTestAdmin();

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> adminService.changeAccess(adminId, new ChangeAdminAccessRequest(false)))
                .isInstanceOf(UserNotPermissionsException.class)
                .hasMessageContaining("Нельзя изменять доступ для пользователя с ролью администратора");
    }

    private Admin getTestAdmin() {
        return Admin.builder()
                .id(1L)
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();
    }
}
