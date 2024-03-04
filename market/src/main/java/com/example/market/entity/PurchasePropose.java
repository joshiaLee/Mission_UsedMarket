package com.example.market.entity;

import com.example.market.dto.PurchaseProposeDto;
import com.example.market.enums.Status;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePropose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private Long itemId;
    @Setter
    private Long shopId;
    @Setter
    private Integer quantity;
    @Setter
    private Status status;

    public static PurchasePropose fromEntity(PurchaseProposeDto proposeDto){
        return PurchasePropose.builder()
                .id(proposeDto.getId())
                .itemId(proposeDto.getItemId())
                .shopId(proposeDto.getShopId())
                .quantity(proposeDto.getQuantity())
                .status(proposeDto.getStatus())
                .build();
    }
}
