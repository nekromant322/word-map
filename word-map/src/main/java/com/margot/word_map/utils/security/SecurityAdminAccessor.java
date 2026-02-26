package com.margot.word_map.utils.security;

import com.margot.word_map.dto.security.AdminDetails;
import com.margot.word_map.exception.UserNotFoundException;
import com.margot.word_map.exception.UserNotPermissionsException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Language;
import com.margot.word_map.model.Rule;
import com.margot.word_map.model.enums.Role;
import com.margot.word_map.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SecurityAdminAccessor {

    private final AdminRepository adminRepository;
    private final ThreadLocalAdminCache adminCache;

    public Role getRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> Role.valueOf(a.substring(5)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("role is not set for security user"));
    }

    public Set<Rule.RULE> getRules(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith("ROLE_"))
                .map(Rule.RULE::valueOf)
                .collect(Collectors.toSet());
    }

    public boolean hasRole(Authentication auth, Role role) {
        return Optional.ofNullable(auth)
                .map(Authentication::getAuthorities)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role.name()));
    }

    public boolean hasRule(Authentication auth, Rule.RULE rule) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(rule.name()));
    }

    public Long getCurrentAdminId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AdminDetails adminDetails) {
            return adminDetails.getId();
        }

        throw new UserNotFoundException();
    }

    @Transactional(readOnly = true)
    public Admin getCurrentAdminReference() {
        return adminRepository.getReferenceById(getCurrentAdminId());
    }

    @Transactional(readOnly = true)
    public Admin getCurrentAdmin() {
        if (!adminCache.isEmpty()) {
            return adminCache.getAdmin();
        }

        Admin admin = adminRepository.findWithLangById(getCurrentAdminId())
                .orElseThrow(UserNotFoundException::new);
        adminCache.setAdmin(admin);

        return admin;
    }

    @Transactional(readOnly = true)
    public void checkLanguageAccess(Language language) {
        Admin admin = getCurrentAdmin();
        if (admin.getRole() == Role.ADMIN) return;
        boolean hasAccess = admin.getLanguages().stream()
                .anyMatch(adminLang -> adminLang.getLanguage().equals(language));

        if (!hasAccess) {
            throw new UserNotPermissionsException(
                    "Недостаточно прав для доступа к ресурсу с языком: " + language.getName());
        }
    }
}
