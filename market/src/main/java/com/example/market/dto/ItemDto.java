package com.example.market.dto;

import com.example.market.entity.Item;
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
    private String status;

    private Long user_id;

    public static ItemDto fromEntity(Item item){
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .content(item.getContent())
                .price(item.getPrice())
                .status(item.getStatus())
                .user_id(item.getUserEntity().getId())
                .build();
    }

}
