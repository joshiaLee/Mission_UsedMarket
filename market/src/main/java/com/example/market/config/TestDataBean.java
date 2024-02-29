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
                        .email("user@gmail.com")
                        .phone("01012345678")
                        .authorities("ROLE_USER,READ_AUTHORITY")
                        .build());

        manager.createUser(CustomUserDetails.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .email("admin@gmail.com")
                .phone("01079334262")
                .authorities("ROLE_ADMIN,ROLE_USER,WRITE_AUTHORITY,READ_AUTHORITY")
                .build());
    }
}
