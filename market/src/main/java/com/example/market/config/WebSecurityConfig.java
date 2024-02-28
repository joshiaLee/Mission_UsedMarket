package com.example.market.config;

import com.example.market.filters.JwtTokenFilter;
import com.example.market.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http
                // csrf 보안 헤제
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/users/home",
                                "/token/issue",
                                "/token/validate"
                        )
                        .permitAll()
                        .requestMatchers("/users/my-profile")
                        .authenticated()
                        .requestMatchers(
                                "/users/login",
                                "/users/register"
                        )
                        .anonymous()
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
        // JWT 이전
        /*
                http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(
                                        "/users/home",
                                        "/token/issue",
                                        "/token/validate")
                                .permitAll()
                                .requestMatchers("/users/my-profile")
                                .authenticated()
                                .requestMatchers(
                                        "/users/login",
                                        "/users/register")
                                .anonymous()
                                .anyRequest()
                                .authenticated()
                )
                // 폼로그인이 UserDetailsManager Or 구현체를 사용한다.
                .formLogin(
                        formLogin -> formLogin
                                .loginPage("/users/login")
                                .defaultSuccessUrl("/users/my-profile")
                                .failureUrl("/users/login?fail")
                )
                .logout(logout -> logout
                        // 어떤 경로(URL)로 요청을 보내면 로그아웃이 되는지
                        // 즉 세션을 삭제한
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/users/home")
                )
                        .addFilterBefore(
                                new JwtTokenFilter(jwtTokenUtils),
                                AuthorizationFilter.class
                        )
        ;

         */

        return http.build();
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
