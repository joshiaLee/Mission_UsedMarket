package com.example.market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Entity
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private UserEntity userEntity;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;


    private String uploadFileName;
    private String storeFileName;


    public void associateImageWithEntity(Object objectEntity, String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
        if(objectEntity instanceof UserEntity)
            this.userEntity = (UserEntity) objectEntity;

        // 연관관계 편의 메서드
        if(objectEntity instanceof Item) {
            this.item = (Item) objectEntity;
            item.getImageEntities().add(this);
        }
    }

}
