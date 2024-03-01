package com.example.market.article.dto;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    // 필수 정보
    private String username;

    private String password;
    private String passwordCheck;

    // 추가 정보
    private String nickname;

    private String name;
    private Integer age;

    private String email;
    private String phone;


}
