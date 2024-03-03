package com.example.market.entity;

import com.example.market.enums.Status;
import com.example.market.dto.UserDto;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Builder
@Entity
@Table(name = "users")
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
    @Enumerated(EnumType.STRING)
    private Status status;

    @Setter
    private String authorities;



    public static UserEntity fromUserDto(UserDto userDto) {
        return com.example.market.entity.UserEntity.builder()
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .nickname(userDto.getNickname())
                .name(userDto.getName())
                .age(userDto.getAge())
                .email(userDto.getEmail())
                .phone(userDto.getPhone())
                .registrationNumber(userDto.getRegistrationNumber())
                .status(userDto.getStatus())
                .authorities(userDto.getAuthorities())
                .build();
    }


}
