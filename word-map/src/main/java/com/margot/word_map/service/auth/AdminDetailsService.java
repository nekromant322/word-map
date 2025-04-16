package com.margot.word_map.service.auth;

import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepository.findByEmail(email)
                .map(admin -> (UserDetails) admin)
                .or(() -> userRepository.findByEmail(email).map(user -> (UserDetails) user))
                .orElseThrow(() -> new UsernameNotFoundException("user not found" + email));
    }
}
