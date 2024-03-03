package com.example.market.entity;

import com.example.market.enums.Category;
import com.example.market.enums.Status;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String name;
    @Setter
    private String introduction;
    @Setter
    @Enumerated(EnumType.STRING)
    private Category category;

    @Setter
    @OneToOne
    @JoinColumn(name = "users_id")
    private UserEntity userEntity;
    @Enumerated(EnumType.STRING)
    private Status status;
}
