package com.example.market.config;

import com.example.market.entity.UserEntity;
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
                        .username("user")
                        .password(passwordEncoder.encode("password"))
                        .nickname("짱구")
                        .name("user1")
                        .age(18)
                        .email("user1@gmail.com")
                        .phone("01012345678")
                        .authorities("ROLE_USER")
                        .build());

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

        service.createUser(UserEntity.builder()
                .username("이강선")
                .password(passwordEncoder.encode("password"))
                .nickname("강마에")
                .name("user2")
                .age(28)
                .email("user2@gmail.com")
                .phone("01012345678")
                .authorities("ROLE_USER")
                .registrationNumber("1234")
                .status("Proceeding")
                .build());

        service.createUser(UserEntity.builder()
                .username("당근")
                .password(passwordEncoder.encode("password"))
                .nickname("당근당근")
                .name("user3")
                .age(20)
                .email("user3@gmail.com")
                .phone("01012345678")
                .authorities("ROLE_USER")
                .registrationNumber("12345")
                .status("Proceeding")
                .build());

        service.createUser(UserEntity.builder()
                .username("비활성")
                .password(passwordEncoder.encode("password"))
                .authorities("ROLE_UNACTIVATED")
                .build());
    }
}
