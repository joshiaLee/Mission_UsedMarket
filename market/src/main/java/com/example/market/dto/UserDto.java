package com.example.market.dto;

import com.example.market.entity.UserEntity;
import com.example.market.enums.Status;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    // 필수 정보
    private Long id;

    private String username;

    private String password;
    private String passwordCheck;

    // 추가 정보
    private String nickname;

    private String name;
    private Integer age;

    private String email;
    private String phone;

    private String registrationNumber;

    private Status status;
    private String authorities;

    public static UserDto fromEntity(UserEntity userEntity){
        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .nickname(userEntity.getNickname())
                .name(userEntity.getName())
                .age(userEntity.getAge())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .registrationNumber(userEntity.getRegistrationNumber())
                .status(userEntity.getStatus())
                .authorities(userEntity.getAuthorities())
                .build();
    }


}
