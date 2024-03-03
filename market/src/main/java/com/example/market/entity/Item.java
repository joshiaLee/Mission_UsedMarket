package com.example.market.entity;

import com.example.market.dto.ItemDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String name;
    @Setter
    private String content;

    @Setter
    private Integer price;

    @Setter
    private String status;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private UserEntity userEntity;

    // orphanRemoval 설정을 위해 양방향 매핑함
    @Setter
    @OneToMany(mappedBy = "item", orphanRemoval = true)
    private List<ImageEntity> imageEntities = new ArrayList<>();


    public static Item fromDto(ItemDto itemDto){
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .content(itemDto.getContent())
                .price(itemDto.getPrice())
                .status(itemDto.getStatus())
                .build();

    }



}
