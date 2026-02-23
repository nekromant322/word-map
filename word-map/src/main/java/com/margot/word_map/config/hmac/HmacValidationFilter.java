package com.margot.word_map.config.hmac;

import com.margot.word_map.model.enums.Role;
import com.margot.word_map.service.auth.PlayerDetailsService;
import com.margot.word_map.service.hmac.HmacService;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component

public class HmacValidationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver resolver;

    private final HmacService hmacService;

    private final PlayerDetailsService playerService;

    private final SecurityAdminAccessor adminAccessor;

    public HmacValidationFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                                HmacService hmacService,
                                PlayerDetailsService playerDetailsService,
                                SecurityAdminAccessor adminAccessor) {
        this.resolver = resolver;
        this.hmacService = hmacService;
        this.playerService = playerDetailsService;
        this.adminAccessor = adminAccessor;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
            String json = new String(body, StandardCharsets.UTF_8);
            String username = hmacService.extractEmail(json);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = adminAccessor.hasRole(auth, Role.ADMIN) || adminAccessor.hasRole(auth, Role.MODERATOR);
            if (!isAdmin) {
                String signature = request.getHeader("player_signature");
                if (signature == null || !hmacService.isValid(signature, json)) {
                    throw new BadCredentialsException("Отсутствует player_signature или заголовок");
                }
            }
            CachedBodyServletInputStream requestWrapper = new CachedBodyServletInputStream(request, body);
            UserDetails details = playerService.loadUserByUsername(username);
            if (!isAdmin) {
                UsernamePasswordAuthenticationToken authToken
                        = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            filterChain.doFilter(requestWrapper, response);
        } catch (Exception e) {
            resolver.resolveException(request, response, null, e);
        }
    }

    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !path.startsWith("/user");
    }
}
