package org.valdon.abtests.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.valdon.abtests.props.CorsProperties;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsProperties corsProperties;

    @Bean
    public CorsConfigurationSource cors() {
        var cors = new CorsConfiguration();
        cors.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "DELETE", "PUT"));
        cors.setAllowCredentials(true);
        cors.setAllowedHeaders(List.of("*"));
        cors.setAllowedOrigins(corsProperties.getAllowedOrigins());
        cors.setMaxAge(3600L);

        var config = new UrlBasedCorsConfigurationSource();
        config.registerCorsConfiguration("/**", cors);
        return config;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors
                        .configurationSource(cors()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .anyRequest().permitAll())
                .exceptionHandling(handle -> handle
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("\"message\":\"Access Denied\"");
                        }));
        return http.build();
    }

}
