package com.example.market.dto;

import com.example.market.entity.Propose;
import com.example.market.enums.Status;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProposeDto {
    private Long id;
    private Long itemId;
    private Long sellerId;
    private Long buyerId;
    private Status status;

    public static ProposeDto fromEntity(Propose propose){
        return ProposeDto.builder()
                .id(propose.getId())
                .itemId(propose.getItemId())
                .sellerId(propose.getSellerId())
                .buyerId(propose.getBuyerId())
                .status(propose.getStatus())
                .build();
    }

}
