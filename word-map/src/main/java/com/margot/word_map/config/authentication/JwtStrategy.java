package com.margot.word_map.config.authentication;

import com.margot.word_map.exception.UserNotAccessException;
import com.margot.word_map.service.auth.AdminDetailsService;
import com.margot.word_map.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class JwtStrategy implements AuthenticationStrategy {
    private final JwtService jwtService;

    private final AdminDetailsService adminDetailsService;

    public JwtStrategy(JwtService jwtService,
                       AdminDetailsService adminDetailsService) {
        this.jwtService = jwtService;
        this.adminDetailsService = adminDetailsService;
    }

    @Override
    public boolean supports(HttpServletRequest request, Authentication authentication) {
        String token = request.getHeader("Authorization");
        return token != null && token.startsWith("Bearer ");
    }

    @Override
    public void authenticate(HttpServletRequest request) throws Exception {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtService.extractUserName(token);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails details = adminDetailsService.loadUserByUsername(username);
            if (!details.isAccountNonLocked()) {
                throw new UserNotAccessException("Аккаунт заблокирован : " + details.getUsername());
            }

            if (jwtService.validateToken(token, details)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }
}
