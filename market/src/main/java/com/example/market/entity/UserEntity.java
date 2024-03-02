package com.example.market.entity;

import com.example.market.dto.CustomUserDetails;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Builder
@Entity
@Table(name = "user_table")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Setter
    private String password;

    @Setter
    private String nickname;

    @Setter
    private String name;
    @Setter
    private Integer age;
    @Setter
    private String email;
    @Setter
    private String phone;

    @Setter
    private String registrationNumber;

    @Setter
    private String status;

    @Setter
    private String authorities;


    public static UserEntity fromCustomUserDetails(CustomUserDetails customUser) {
        return UserEntity.builder()
                .username(customUser.getUsername())
                .password(customUser.getPassword())
                .nickname(customUser.getNickname())
                .name(customUser.getName())
                .age(customUser.getAge())
                .email(customUser.getEmail())
                .phone(customUser.getPhone())
                .registrationNumber(customUser.getRegistrationNumber())
                .status(customUser.getStatus())
                .authorities(customUser.getRawAuthorities())
                .build();
    }

    public static void setUserEntity(UserEntity updateEntity, CustomUserDetails customUser) {
        updateEntity.setPassword(customUser.getPassword());
        updateEntity.setNickname(customUser.getNickname());
        updateEntity.setName(customUser.getName());
        updateEntity.setAge(customUser.getAge());
        updateEntity.setEmail(customUser.getEmail());
        updateEntity.setPhone(customUser.getPhone());
        updateEntity.setRegistrationNumber(customUser.getRegistrationNumber());
        updateEntity.setStatus(customUser.getStatus());
        updateEntity.setAuthorities(customUser.getRawAuthorities());
    }
}
