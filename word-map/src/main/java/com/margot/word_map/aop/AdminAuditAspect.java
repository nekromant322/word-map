package com.margot.word_map.aop;

import com.margot.word_map.aop.annotation.Audited;
import com.margot.word_map.exception.AuditException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.service.audit.AuditContext;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminAuditAspect {

    private final SecurityAdminAccessor adminAccessor;
    private final AuditService auditService;
    private final AdminRepository adminRepository;

    @AfterReturning("@annotation(audited)")
    public void audit(Audited audited) {

        try {
            Long adminId = AuditContext.contains("adminId") ?
                    (Long) AuditContext.get("adminId") :
                    adminAccessor.getCurrentAdminId();

            Admin admin = adminRepository.getReferenceById(adminId);
            String actionStr = audited.action().render(AuditContext.get());

            auditService.log(admin, actionStr);
        } catch (Exception e) {
            throw new AuditException("admin id must either be retrievable from security context or audit context");
        }
    }
}
