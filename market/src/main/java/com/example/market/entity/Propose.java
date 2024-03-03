package com.example.market.entity;

import com.example.market.dto.ProposeDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Propose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private Long itemId;
    @Setter
    private Long sellerId;
    @Setter
    private Long buyerId;
    @Setter
    private String status;

    @CreationTimestamp
    private LocalDateTime creationTime;

    public static Propose fromEntity(ProposeDto proposeDto){
        return Propose.builder()
                .id(proposeDto.getId())
                .itemId(proposeDto.getItemId())
                .sellerId(proposeDto.getSellerId())
                .buyerId(proposeDto.getBuyerId())
                .status(proposeDto.getStatus())
                .build();
    }

}
