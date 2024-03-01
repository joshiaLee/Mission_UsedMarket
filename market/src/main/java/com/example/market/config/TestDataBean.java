package com.example.market.config;

import com.example.market.entity.CustomUserDetails;
import com.example.market.service.JPAUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestDataBean {

    public TestDataBean(
            JPAUserDetailsManager manager,
            PasswordEncoder passwordEncoder
    ){
        manager.createUser(CustomUserDetails.builder()
                        .username("user")
                        .password(passwordEncoder.encode("password"))
                        .nickname("짱구")
                        .name("user")
                        .age(18)
                        .email("user@gmail.com")
                        .phone("01012345678")
                        .authorities("ROLE_USER")
                        .build());

        manager.createUser(CustomUserDetails.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .nickname("철수")
                .name("admin")
                .age(28)
                .email("hhhjs0133@gmail.com")
                .phone("01079334262")
                .authorities("ROLE_ADMIN")
                .build());
    }
}
