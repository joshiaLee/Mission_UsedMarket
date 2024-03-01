package com.example.market.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "image_entity")
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_table_id")
    private UserEntity userEntity;

}
