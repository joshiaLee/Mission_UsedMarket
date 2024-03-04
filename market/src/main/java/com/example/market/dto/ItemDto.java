package com.example.market.dto;

import com.example.market.entity.Item;
import com.example.market.enums.Status;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String content;
    private Integer price;
    private Integer stock;
    private Status status;


    public static ItemDto fromEntity(Item item){
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .content(item.getContent())
                .price(item.getPrice())
                .stock(item.getStock())
                .status(item.getStatus())
                .build();
    }

}
