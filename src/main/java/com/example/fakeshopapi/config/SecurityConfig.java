package com.example.fakeshopapi.config;

import com.example.fakeshopapi.security.jwt.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.apply(authenticationManagerConfig);

        return http
                .sessionManagement(sessionManager ->
                        sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(withDefaults())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                        .requestMatchers("/signup", "/login", "/users/refresh").permitAll()
                        .requestMatchers("/**").hasAnyRole("USER", "MANAGER", "ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint))
                .build();

    }

    // <<Advanced>> Security Cors로 변경 시도
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // config.setAllowCredentials(true); // 이거 빼면 된다
        // https://gareen.tistory.com/66
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.setAllowedMethods(List.of("GET","POST","DELETE","PATCH","OPTION","PUT"));
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
