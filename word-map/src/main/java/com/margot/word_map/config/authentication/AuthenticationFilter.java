package com.margot.word_map.config.authentication;

import com.margot.word_map.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver resolver;

    private final StrategyManager strategyManager;

    private final RequestAttributeSecurityContextRepository repo;

    public AuthenticationFilter(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver,
                                StrategyManager strategyManager,
                                RequestAttributeSecurityContextRepository repo) {
        this.resolver = resolver;
        this.strategyManager = strategyManager;
        this.repo = repo;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            byte[] body = StreamUtils.copyToByteArray(request.getInputStream());

            CachedBodyHttpServletRequest requestWrapper = new CachedBodyHttpServletRequest(request, body);

            strategyManager.authenticate(requestWrapper);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                repo.saveContext(SecurityContextHolder.getContext(), request, response);
            }

            filterChain.doFilter(requestWrapper, response);
        } catch (ExpiredJwtException e) {
            resolver.resolveException(request, response, null, new InvalidTokenException("Expired JWT token"));
        } catch (JwtException e) {
            resolver.resolveException(request, response, null, new InvalidTokenException());
        } catch (Exception e) {
            resolver.resolveException(request, response, null, e);
        }
    }
}