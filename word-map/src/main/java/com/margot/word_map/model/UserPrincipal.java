package com.margot.word_map.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final Object principal;

    public UserPrincipal(Object principal) {
        if (!(principal instanceof User) && !(principal instanceof Admin)) {
            throw new IllegalArgumentException("Principal must be User or Admin");
        }
        this.principal = principal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = principal instanceof User
                ? "ROLE_" + ((User) principal).getRole().name()
                : "ROLE_" + ((Admin) principal).getRole().name();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return principal instanceof User
                ? ((User) principal).getEmail()
                : ((Admin) principal).getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return principal instanceof User
                ? ((User) principal).getAccess()
                : ((Admin) principal).getAccess();
    }
}
