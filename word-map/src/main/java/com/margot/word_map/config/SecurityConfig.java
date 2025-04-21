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

import java.util.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AdminDetailsService myUserDetailsService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomAuthenticationEntryPoint customEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    private static final Map<String, Rule.RULE> PATH_TO_RULE_MAP = new LinkedHashMap<>();

    static {
        PATH_TO_RULE_MAP.put("/dictionary/wipe", Rule.RULE.WIPE_DICTIONARY);
        PATH_TO_RULE_MAP.put("/dictionary", Rule.RULE.MANAGE_DICTIONARY);
        PATH_TO_RULE_MAP.put("/rating", Rule.RULE.MANAGE_RATING);
        PATH_TO_RULE_MAP.put("/world", Rule.RULE.MANAGE_WORLD);
        PATH_TO_RULE_MAP.put("/admins", Rule.RULE.MANAGE_ROLE);
        PATH_TO_RULE_MAP.put("/ivent", Rule.RULE.MANAGE_IVENT);
        PATH_TO_RULE_MAP.put("/shop", Rule.RULE.MANAGE_SHOP);
    }

    private static final List<String> USER_PATHS = List.of(
            "/user"
    );

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/admins").access(authorizationManager())
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .anyRequest().access(authorizationManager())
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
    public AuthorizationManager<RequestAuthorizationContext> authorizationManager() {
        return (authenticationSupplier, context) -> {
            Authentication authentication = authenticationSupplier.get();
            HttpServletRequest request = context.getRequest();
            return checkAccessByUrl(authentication, request);
        };
    }

    private AuthorizationDecision checkAccessByUrl(Authentication authentication, HttpServletRequest request) {
        if (authentication.getPrincipal() instanceof Admin admin) {
            String path = request.getRequestURI();
            if (matchesPath(path, USER_PATHS)) {
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
                    matchesPath(request.getRequestURI(), USER_PATHS)
            );
        }
        return new AuthorizationDecision(false);
    }

    private boolean matchesPath(String path, List<String> prefixes) {
        return prefixes.stream().anyMatch(path::startsWith);
    }

    private Optional<Rule.RULE> getRuleByPath(String path) {
        return PATH_TO_RULE_MAP.entrySet().stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }
}
