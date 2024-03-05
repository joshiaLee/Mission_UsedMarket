package com.example.market.config;

import com.example.market.entity.UserEntity;
import com.example.market.enums.Status;
import com.example.market.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class TestDataBean {

    public TestDataBean(
            UserService service,
            PasswordEncoder passwordEncoder
    ){

        service.createUser(UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .nickname("철수")
                .name("admin")
                .age(28)
                .email("hhhjs0133@gmail.com")
                .phone("01079334262")
                .authorities("ROLE_ADMIN")
                .build());

        for (int i = 1; i <= 49; i++) {
            service.createUser(UserEntity.builder()
                    .username("user" + i)
                    .password(passwordEncoder.encode("password"))
                    .nickname("user" + i)
                    .name("user" + i)
                    .age(28)
                    .email("user" + i + "@gmail.com")
                    .phone(String.valueOf(i))
                    .authorities("ROLE_CEO")
                    .registrationNumber(String.valueOf(i))
                    .status(Status.ADMITTED)
                    .build());
        }

        service.createUser(UserEntity.builder()
                .username("몽실")
                .password(passwordEncoder.encode("password"))
                .authorities("ROLE_UNACTIVATED")
                .build());

    }
}
