package com.example.market.dto;

import com.example.market.entity.ShopPropose;
import com.example.market.enums.Status;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShopProposeDto {

    private Long id;

    private Long shopId;
    private String message;

    private Status status;

    public static ShopProposeDto fromEntity(ShopPropose shopPropose){
        return ShopProposeDto.builder()
                .id(shopPropose.getId())
                .shopId(shopPropose.getShopId())
                .message(shopPropose.getMessage())
                .status(shopPropose.getStatus())
                .build();
    }
}
