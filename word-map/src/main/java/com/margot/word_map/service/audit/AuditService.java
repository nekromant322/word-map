package com.margot.word_map.service.audit;

import com.margot.word_map.exception.AuditSaveException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.audit.Audit;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.AuditRepository;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AdminRepository adminRepository;
    private final AuditRepository auditRepository;
    private final SecurityAdminAccessor adminAccessor;

    @Transactional
    public void log(AuditActionType type, Object... args) {
        Admin admin = adminAccessor.getCurrentAdmin();

        log(admin, type, args);
    }

    @Transactional
    public void log(Long adminId, AuditActionType type, Object... args) {
        Admin admin = adminRepository.findById(adminId)
                .orElse(null);

        log(admin, type, args);
    }

    @Transactional
    void log(Admin admin, AuditActionType type, Object... args) {
        validateParameters(admin, type);
        String actionType = type.render(args);

        Audit audit = Audit.builder()
                .admin(admin)
                .email(admin.getEmail())
                .role(admin.getRole())
                .actionType(actionType)
                .createdAt(LocalDateTime.now())
                .build();
        try {
            auditRepository.save(audit);
        } catch (Exception e) {
            throw new AuditSaveException("Не удалось сохранить запись аудита для действия: " + type, e);
        }
    }

    private void validateParameters(Admin admin, AuditActionType type) {
        Objects.requireNonNull(admin, "admin cannot be null");
        Objects.requireNonNull(type, "audit type cannot be null");

        if (admin.getEmail() == null || admin.getRole() == null) {
            throw new IllegalStateException("У администратора должны быть указаны email и роль");
        }
    }
}
