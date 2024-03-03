package com.example.market.dto;

import com.example.market.entity.ImageEntity;
import lombok.*;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private Long id;
    private String uploadFileName;
    private String storeFileName;

    public static ImageDto fromEntity(ImageEntity imageEntity){
        return ImageDto.builder()
                .id(imageEntity.getId())
                .uploadFileName(imageEntity.getUploadFileName())
                .storeFileName(imageEntity.getStoreFileName())
                .build();
    }
}
