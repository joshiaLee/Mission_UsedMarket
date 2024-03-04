package com.example.market.dto;

import com.example.market.entity.Shop;
import com.example.market.enums.Category;
import com.example.market.enums.Status;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShopDto {

    private Long id;
    private String name;
    private String introduction;
    private Category category;

    private Long ownerId;

    private Status status;

    public static ShopDto fromEntity(Shop shop){
        return ShopDto.builder()
                .id(shop.getId())
                .name(shop.getName())
                .introduction(shop.getIntroduction())
                .category(shop.getCategory())
                .ownerId(shop.getUserEntity().getId())
                .status(shop.getStatus())
                .build();
    }
}
