package com.example.market.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
                http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("users/home")
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
                // 폼로그인이 UserDetailsManager를 사용한다.
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
                        .logoutSuccessUrl("/users/login"))
        ;

        return http.build();
    }

    @Bean
    // 사용자 정보 관리 클래스
    public UserDetailsManager userDetailsManager(
            PasswordEncoder passwordEncoder
    ){
        UserDetails user1 = User.withUsername("user1")
                .password(passwordEncoder.encode("password1"))
                .build();

        return new InMemoryUserDetailsManager(user1);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
