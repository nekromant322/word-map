package com.margot.word_map.service.audit;

import com.margot.word_map.model.Admin;
import com.margot.word_map.model.audit.Audit;
import com.margot.word_map.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    @Transactional
    public void log(Admin admin, String actionType) {
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
