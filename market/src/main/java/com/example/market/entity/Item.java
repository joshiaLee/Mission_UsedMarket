package com.example.market.entity;

import com.example.market.dto.ItemDto;
import jakarta.persistence.*;
import lombok.*;

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
    @ManyToOne
    @JoinColumn(name = "user_table_id")
    private UserEntity userEntity;


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
