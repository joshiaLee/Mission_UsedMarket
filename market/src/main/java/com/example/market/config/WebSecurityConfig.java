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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService service;

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
                        // 추가정보 등록
                        .requestMatchers("/users/add-info")
                        .hasRole("UNACTIVATED")
                        // 이미지 등록
                        .requestMatchers("/users/add-image")
                        .hasAnyRole("UNACTIVATED", "USER")
                        // 사업자 등록 신청
                        .requestMatchers("/users/apply", "/users/add-item")
                        .hasRole("USER")
                        // 내 프로필 확인
                        .requestMatchers("/users/my-profile")
                        .authenticated()
                        // 로그인 화면, 회원가입
                        .requestMatchers(
                                "/users/login",
                                "/users/register"
                        )
                        .anonymous()
                        .requestMatchers("/auth/user-role")
                        .hasAnyRole("USER", "ADMIN")
                        // 관리자 권한(사업자 목록 확인, 승인, 거절)
                        .requestMatchers("/auth/admin-role", "/admin/apply-list", "/admin/apply-admit/{id}", "/admin/apply-reject/{id}")
                        .hasRole("ADMIN")
                        .requestMatchers("/auth/read-authority")
                        .hasAnyAuthority("READ_AUTHORITY", "WRITE_AUTHORITY")
                        .requestMatchers("/auth/write-authority")
                        .hasAuthority("WRITE_AUTHORITY")
                        .anyRequest()
                        .permitAll()
                )
                // JWT 사용하기 때문에 보안 관련 세션 해제 (STATELESS: 상태를 저장하지 않음)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtTokenFilter(jwtTokenUtils, service),
                        AuthorizationFilter.class
                );

        return http.build();
    }




}
