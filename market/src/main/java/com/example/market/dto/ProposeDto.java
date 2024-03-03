package com.example.market.dto;

import com.example.market.entity.Propose;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProposeDto {
    private Long id;
    private Long item_id;
    private Long seller_id;
    private Long buyer_id;
    private String status;

    public static ProposeDto fromEntity(Propose propose){
        return ProposeDto.builder()
                .id(propose.getId())
                .item_id(propose.getItem_id())
                .seller_id(propose.getSeller_id())
                .buyer_id(propose.getBuyer_id())
                .status(propose.getStatus())
                .build();
    }

}
