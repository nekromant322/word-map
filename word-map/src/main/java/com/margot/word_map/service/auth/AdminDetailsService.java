package com.margot.word_map.service.auth;

import com.margot.word_map.dto.security.AdminDetails;
import com.margot.word_map.exception.UserNotFoundException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public AdminDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepository.findByEmail(email)
                .map(this::from)
                .orElseThrow(() -> new UserNotFoundException("admin not found" + email));
    }

    public AdminDetails from(Admin admin) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        admin.getRules()
                .forEach(r -> authorities.add(new SimpleGrantedAuthority(r.getAuthority())));
        authorities.add(new SimpleGrantedAuthority("ROLE_" + admin.getRole()));

        return AdminDetails.builder()
                .id(admin.getId())
                .email(admin.getEmail())
                .accessGranted(admin.isAccessGranted())
                .authorities(authorities)
                .build();
    }
}
