package com.margot.word_map.config;

import com.margot.word_map.config.jwt.JwtFilter;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
import com.margot.word_map.service.auth.AdminDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.swing.text.html.Option;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AdminDetailsService myUserDetailsService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomAuthenticationEntryPoint customEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/admins").authenticated()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/dictionary/**").authenticated()
                        .requestMatchers("/roles/**").authenticated()
                        .requestMatchers("/wordsOffer/**").authenticated()
                        .requestMatchers("/**").authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization")
                        )
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customEntryPoint)
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

//    private AuthorizationDecision customAccessCheck(Authentication authentication, HttpServletRequest request) {
//        Admin admin = (Admin) authentication.getPrincipal();
//
//        if (admin.getRole() == Admin.ROLE.ADMIN) {
//            return new AuthorizationDecision(true);
//        }
//
//        String path = request.getRequestURI();
//        Optional<Rule.RULE> requiredRule = getRuleByPath(path); // ты сам настраиваешь соответствие
//
//        return new AuthorizationDecision(
//                requiredRule.map(rule -> admin.getRules().stream()
//                                .map(Rule::getName)
//                                .anyMatch(r -> r == rule))
//                        .orElse(false)
//        );
//    }

//    private Optional<Rule.RULE> getRuleByPath(String path) {
//        if (path.startsWith("/dictionary/wipe")) {
//            return Optional.of(Rule.RULE.WIPE_DICTIONARY);
//        }
//        if (path.startsWith("/dictionary")) {
//            return Optional.of(Rule.RULE.MANAGE_DICTIONARY);
//        }
//        if (path.startsWith("/rating")) {
//            return Optional.of(Rule.RULE.MANAGE_RATING);
//        }
//        if (path.startsWith("/world")) {
//            return Optional.of(Rule.RULE.MANAGE_WORLD);
//        }
//        if (path.startsWith("/auth/admins") || path.startsWith("/roles")) {
//            return Optional.of(Rule.RULE.MANAGE_ROLE);
//        }
//        if (path.startsWith("/ivent")) {
//            return Optional.of(Rule.RULE.MANAGE_IVENT);
//        }
//        if (path.startsWith("/shop")) {
//            return Optional.of(Rule.RULE.MANAGE_SHOP);
//        }
//        return Optional.empty();
//    }
}
