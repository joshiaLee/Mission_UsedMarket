package com.example.market.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


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
    private Long registrationNumber;

    @Setter
    private String authorities;

    @OneToMany(mappedBy = "userEntity")
    private List<ImageEntity> imageEntityList = new ArrayList<>();
}
