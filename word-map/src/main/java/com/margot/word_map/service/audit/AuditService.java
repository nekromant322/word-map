package com.margot.word_map.service.audit;

import com.margot.word_map.model.Admin;
import com.margot.word_map.model.audit.Audit;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.AuditRepository;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AdminRepository adminRepository;
    private final AuditRepository auditRepository;
    private final SecurityAdminAccessor adminAccessor;

    @Transactional
    public void log(AuditActionType type, String... args) {
        Long adminId = adminAccessor.getCurrentAdminId();
        log(adminId, type, args);
    }

    @Transactional
    public void log(Long adminId, AuditActionType type, String... args) {
        String actionType = type.render(args);
        Admin admin = adminRepository.getReferenceById(adminId);

        Audit audit = Audit.builder()
                .admin(admin)
                .email(admin.getEmail())
                .role(admin.getRole())
                .actionType(actionType)
                .createdAt(LocalDateTime.now())
                .build();

        auditRepository.save(audit);
    }
}
