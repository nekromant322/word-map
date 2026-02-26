package com.margot.word_map.config.authentication;

import com.margot.word_map.exception.UserNotAccessException;
import com.margot.word_map.service.auth.AdminDetailsService;
import com.margot.word_map.service.auth.PlayerDetailsService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.signature.PlayerSignatureService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@Order(1)
public class JwtStrategy implements AuthenticationStrategy {
    private final JwtService jwtService;

    private final AdminDetailsService adminDetailsService;

    private final PlayerDetailsService playerService;

    private final PlayerSignatureService playerSignatureService;

    private final HandlerExceptionResolver resolver;

    public JwtStrategy(JwtService jwtService,
                       AdminDetailsService adminDetailsService,
                       PlayerDetailsService playerService,
                       PlayerSignatureService playerSignatureService,
                       @Qualifier("handlerExceptionResolver")
                       HandlerExceptionResolver resolver) {
        this.jwtService = jwtService;
        this.adminDetailsService = adminDetailsService;
        this.playerService = playerService;
        this.playerSignatureService = playerSignatureService;
        this.resolver = resolver;
    }

    @Override
    public boolean supports(HttpServletRequest request, Authentication authentication) {
        String token = request.getHeader("Authorization");
        return token != null && token.startsWith("Bearer ");
    }

    @Override
    public void authenticate(HttpServletRequest request, String json) throws Exception {
        String token = request.getHeader("Authorization").substring(7);
        String username = jwtService.extractUserName(token);
        String playerUsername = null;
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails details = adminDetailsService.loadUserByUsername(username);
            if (!details.isAccountNonLocked()) {
                throw new UserNotAccessException("Аккаунт заблокирован : " + details.getUsername());
            }

            if (jwtService.validateToken(token, details)) {
                if (request.getServletPath().startsWith("/user")) {
                    playerUsername = playerSignatureService.extractEmail(json);
                    playerService.loadUserByUsername(playerUsername);
                }
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
    }
}
