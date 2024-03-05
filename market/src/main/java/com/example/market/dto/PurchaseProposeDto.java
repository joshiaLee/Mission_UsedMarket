package com.example.market.dto;

import com.example.market.entity.PurchasePropose;
import com.example.market.enums.Status;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseProposeDto {
    private Long id;
    private Long itemId;
    private Long shopId;
    private Long userId;
    private Integer quantity;
    private Status status;

    public static PurchaseProposeDto fromEntity(PurchasePropose propose){
        return PurchaseProposeDto.builder()
                .id(propose.getId())
                .itemId(propose.getItemId())
                .shopId(propose.getShopId())
                .userId(propose.getUserId())
                .quantity(propose.getQuantity())
                .status(propose.getStatus())
                .build();
    }
}
