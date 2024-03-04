package com.example.market.config;

import com.example.market.filters.JwtTokenFilter;
import com.example.market.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
                        // 추가정보 등록
                        .requestMatchers("/users/add-info")
                        .hasRole("UNACTIVATED")
                        // 이미지 등록
                        .requestMatchers("/users/add-image")
                        .hasAnyRole("UNACTIVATED", "USER")
                        // 사업자 등록 신청
                        .requestMatchers("/users/apply", "/users/add-item", "/users/my-items")
                        .hasRole("USER")
                        .requestMatchers("/shops/update", "/shops/apply-open",
                                "/shops/apply-close", "/shops/my-proposes",
                                "/shops/add-item", "/shops/purchases-list",
                                "/shops/admit-purchase/{purchase_id}", "/shops/reject-purchase/{purchase_id}")
                        .hasRole("CEO")
                        // 내 프로필 확인
                        .requestMatchers("/users/my-profile")
                        .authenticated()
                        // 로그인 화면, 회원가입
                        .requestMatchers(
                                "/users/login",
                                "/users/register"
                        )
                        // 로그인 안한 사용자만 가능
                        .anonymous()
                        // 비활성화 사용자 제외 모두 이용 가능 서비스
                        .requestMatchers("/auth/user-role", "/users/item-list",
                                "/users/delete-item/{item_id}", "/users/update-view/{item_id}",
                                "/users/update-item/{item_id}", "/users/delete-item-image/{image_id}",
                                "/users/shops-list", "/users/shops-search", "/users/items-search",
                                "/users/purchase-item",
                                "/propose/{item_id}", "/propose/received-list",
                                "/propose/sent-list", "/propose/admit/{propose_id}",
                                "/propose/reject/{propose_id}", "/propose/admit-list",
                                "/propose/admit-list/{propose_id}")
                        .hasAnyRole("USER", "ADMIN", "CEO")
                        // 관리자 권한(사업자 목록 확인, 승인, 거절)
                        .requestMatchers("/auth/admin-role", "/admin/apply-list",
                                "/admin/apply-admit/{id}", "/admin/apply-reject/{id}",
                                "/admin/shops-list", "/admin/shops/{shop_id}",
                                "/admin/admit/{propose_id}", "/admin/reject/{propose_id}",
                                "/admin/closing-shops", "/admin/closing-shops/{propose_id}")
                        .hasRole("ADMIN")
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
