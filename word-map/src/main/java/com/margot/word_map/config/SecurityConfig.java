package com.margot.word_map.config;

import com.margot.word_map.config.filter.AdminCacheCleanupFilter;
import com.margot.word_map.config.jwt.JwtFilter;
import com.margot.word_map.model.Rule;
import com.margot.word_map.model.enums.Role;
import com.margot.word_map.service.auth.AdminDetailsService;
import com.margot.word_map.utils.security.SecurityAdminAccessor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.web.context.SecurityContextHolderFilter;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AdminCacheCleanupFilter adminCacheCleanupFilter;
    private final AdminDetailsService myUserDetailsService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomAuthenticationEntryPoint customEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final SecurityAdminAccessor adminAccessor;

    private static final Map<String, Rule.RULE> PATH_TO_RULE_MAP = new LinkedHashMap<>();

    static {
        PATH_TO_RULE_MAP.put("/dictionary", Rule.RULE.MANAGE_DICTIONARY);
        PATH_TO_RULE_MAP.put("/rating", Rule.RULE.MANAGE_RATING);
        PATH_TO_RULE_MAP.put("/world", Rule.RULE.MANAGE_WORLD);
        PATH_TO_RULE_MAP.put("/event", Rule.RULE.MANAGE_EVENT);
        PATH_TO_RULE_MAP.put("/shop", Rule.RULE.MANAGE_SHOP);
        PATH_TO_RULE_MAP.put("/letter", Rule.RULE.MANAGE_ALPHABET);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/admin/admin/**", "/auth/admin/logout")
                            .access(authorizationManager())
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
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(adminCacheCleanupFilter, SecurityContextHolderFilter.class);

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

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }

    private AuthorizationDecision checkAccessByUrl(Authentication auth, HttpServletRequest request) {
        if (adminAccessor.hasRole(auth, Role.ADMIN)) {
            return new AuthorizationDecision(true);
        }

        String path = request.getRequestURI();
        Optional<Rule.RULE> requiredRule = getRuleByPath(path);
        boolean hasAccess = requiredRule.isPresent() && adminAccessor.hasRule(auth, requiredRule.get());

        return new AuthorizationDecision(hasAccess);
    }

    private Optional<Rule.RULE> getRuleByPath(String path) {
        return PATH_TO_RULE_MAP.entrySet().stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst();
    }
}
