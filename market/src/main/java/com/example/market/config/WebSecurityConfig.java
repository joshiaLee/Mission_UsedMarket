package com.example.market.config;

import com.example.market.filters.JwtTokenFilter;
import com.example.market.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                // csrf 보안 헤제
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/users/home",
                                "/token/issue"
                        )
                        .permitAll()

                        .requestMatchers(HttpMethod.GET, "/articles")
                        .permitAll()

                        .requestMatchers(HttpMethod.POST, "/articles")
                        .authenticated()

                        .requestMatchers("/users/add-info")
                        .hasRole("UNACTIVATED")
                        .requestMatchers("/users/apply")
                        .hasRole("USER")
                        .requestMatchers("/users/my-profile")
                        .authenticated()
                        .requestMatchers(
                                "/users/login",
                                "/users/register"
                        )
                        .anonymous()
                        .requestMatchers("/auth/user-role")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/auth/admin-role")
                        .hasRole("ADMIN")
                        .requestMatchers("/auth/read-authority")
                        .hasAnyAuthority("READ_AUTHORITY", "WRITE_AUTHORITY")
                        .requestMatchers("/auth/write-authority")
                        .hasAuthority("WRITE_AUTHORITY")
                        .anyRequest()
                        .permitAll()
                )
                // JWT를 사용하기 때문에 보안 관련 세션 해제 (STATELESS: 상태를 저장하지 않음)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtTokenFilter(jwtTokenUtils, manager),
                        AuthorizationFilter.class
                );

        return http.build();
    }




}
