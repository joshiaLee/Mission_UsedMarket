package com.example.market.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemsResponseDto {
    private ItemDto itemDto;
    private List<ImageDto> imagesDto;
    private ShopDto shopDto;
}
