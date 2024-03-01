package com.example.market.entity;

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
    private String login;
    @Setter
    private String password;

    @Setter
    private String nickname;

    private String username;
    private Integer age;

    private String email;
    private String phone;

    private Long registrationNumber;


    private String authorities;
}
