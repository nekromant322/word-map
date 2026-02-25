package com.margot.word_map.service.audit;

import com.margot.word_map.exception.AuditSaveException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.audit.Audit;
import com.margot.word_map.model.enums.Role;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.AuditRepository;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private SecurityAdminAccessor adminAccessor;

    @Captor
    private ArgumentCaptor<Audit> auditCaptor;

    @InjectMocks
    private AuditService auditService;

    @Test
    public void testLogAuditWithPassedAdmin() {
        Admin admin = getTestAdmin();

        auditService.log(admin, AuditActionType.ADMIN_UPDATED, "test@example.com");

        verify(auditRepository, times(1)).save(auditCaptor.capture());

        Audit audit = auditCaptor.getValue();

        assertThat(audit.getAdmin()).isEqualTo(admin);
        assertThat(audit.getActionType()).isNotBlank();
        assertThat(audit.getEmail()).isEqualTo(admin.getEmail());
        assertThat(audit.getRole()).isEqualTo(admin.getRole());
    }

    @Test
    public void testLogWithoutAdminParam() {
        Admin admin = getTestAdmin();
        AuditActionType actionType = AuditActionType.ADMIN_UPDATED;
        Object[] args = {"test@example.com"};

        when(adminAccessor.getCurrentAdmin()).thenReturn(admin);

        auditService.log(actionType, args);

        verify(auditRepository).save(any());
    }

    @Test
    public void testLogWithAdminIdParam() {
        Admin admin = getTestAdmin();
        Long adminId = admin.getId();
        AuditActionType actionType = AuditActionType.ADMIN_UPDATED;
        Object[] args = {"test@example.com"};

        when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));

        auditService.log(adminId, actionType, args);

        verify(auditRepository).save(any());
    }

    @Test
    public void testLogThrowsWhenAdminIsNull() {
        assertThatThrownBy(() -> auditService.log((Admin) null, AuditActionType.ADMIN_LOGGED_IN))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("admin cannot be null");
    }

    @Test
    public void testLogThrowsWhenActionTypeIsNull() {
        Admin admin = mock(Admin.class);

        assertThatThrownBy(() -> auditService.log(admin, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("audit type cannot be null");
    }

    @Test
    public void testLogThrowsWhenAdminEmailIsNull() {
        Admin admin = mock(Admin.class);

        assertThatThrownBy(() -> auditService.log(admin, AuditActionType.ADMIN_LOGGED_IN))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("У администратора должны быть указаны email и роль");
    }

    @Test
    public void testLogThrowsWhenAdminRoleIsNull() {
        Admin admin = mock(Admin.class);
        when(admin.getEmail()).thenReturn("test@example.com");
        when(admin.getRole()).thenReturn(null);

        assertThatThrownBy(() -> auditService.log(admin, AuditActionType.ADMIN_LOGGED_IN))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("У администратора должны быть указаны email и роль");
    }

    @Test
    public void testThrowsWhenFailedToSaveAudit() {
        Admin admin = getTestAdmin();
        AuditActionType actionType = AuditActionType.ADMIN_UPDATED;
        Object[] args = {"email"};

        RuntimeException ex = new RuntimeException("failed to connect to DB");
        when(auditRepository.save(any(Audit.class)))
                .thenThrow(ex);

        assertThatThrownBy(() -> auditService.log(admin, actionType, args))
                .isInstanceOf(AuditSaveException.class)
                .hasMessageContaining("Не удалось сохранить запись аудита для действия: %s", actionType)
                .hasCause(ex);
    }

    private Admin getTestAdmin() {
        return Admin.builder()
                .id(1L)
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();
    }
}
