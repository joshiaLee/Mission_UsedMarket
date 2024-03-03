package com.example.market.entity;

import com.example.market.dto.ShopProposeDto;
import com.example.market.enums.Status;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ShopPropose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private Long shopId;
    @Setter
    private String message;

    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;

    public static ShopPropose fromDto(ShopProposeDto shopProposeDto){
        return ShopPropose.builder()
                .id(shopProposeDto.getId())
                .shopId(shopProposeDto.getShopId())
                .message(shopProposeDto.getMessage())
                .status(shopProposeDto.getStatus())
                .build();
    }

}
