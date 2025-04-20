package com.margot.word_map.config;

import com.margot.word_map.config.jwt.JwtFilter;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
import com.margot.word_map.model.User;
import com.margot.word_map.service.auth.AdminDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AdminDetailsService myUserDetailsService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomAuthenticationEntryPoint customEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/admins").access(customAuthorizationManager())
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .anyRequest().access(customAuthorizationManager())
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization")
                        )
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance());
        provider.setUserDetailsService(myUserDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> customAuthorizationManager() {
        return (authenticationSupplier, context) -> {
            Authentication authentication = authenticationSupplier.get();
            HttpServletRequest request = context.getRequest();
            return customAccessCheck(authentication, request);
        };
    }

    private AuthorizationDecision customAccessCheck(Authentication authentication, HttpServletRequest request) {
        if (authentication.getPrincipal() instanceof Admin admin) {
            String path = request.getRequestURI();
            if (isUserPath(path)) {
                return new AuthorizationDecision(false);
            }

            if (admin.getRole() == Admin.ROLE.ADMIN) {
                return new AuthorizationDecision(true);
            }
            Optional<Rule.RULE> requiredRule = getRuleByPath(path);

            return new AuthorizationDecision(
                    requiredRule.map(rule -> admin.getRules().stream()
                                    .map(Rule::getName)
                                    .anyMatch(r -> r == rule))
                            .orElse(false)
            );
        } else if (authentication.getPrincipal() instanceof User user) {
            return new AuthorizationDecision(
                    isUserPath(request.getRequestURI())
            );
        }
        return new AuthorizationDecision(false);
    }

    private boolean isUserPath(String path) {
        if (path.startsWith("/user")) {
            return true;
        }
        return false;
    }

    private Optional<Rule.RULE> getRuleByPath(String path) {
        if (path.startsWith("/dictionary/wipe")) {
            return Optional.of(Rule.RULE.WIPE_DICTIONARY);
        }
        if (path.startsWith("/dictionary")) {
            return Optional.of(Rule.RULE.MANAGE_DICTIONARY);
        }
        if (path.startsWith("/rating")) {
            return Optional.of(Rule.RULE.MANAGE_RATING);
        }
        if (path.startsWith("/world")) {
            return Optional.of(Rule.RULE.MANAGE_WORLD);
        }
        if (path.startsWith("/auth/admins") || path.startsWith("/roles")) {
            return Optional.of(Rule.RULE.MANAGE_ROLE);
        }
        if (path.startsWith("/ivent")) {
            return Optional.of(Rule.RULE.MANAGE_IVENT);
        }
        if (path.startsWith("/shop")) {
            return Optional.of(Rule.RULE.MANAGE_SHOP);
        }
        return Optional.empty();
    }
}
