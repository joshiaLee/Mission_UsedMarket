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
    private Long item_id;
    @Setter
    private Long seller_id;
    @Setter
    private Long buyer_id;
    @Setter
    private String status;

    @CreationTimestamp
    private LocalDateTime creationTime;

    public static Propose fromEntity(ProposeDto proposeDto){
        return Propose.builder()
                .id(proposeDto.getId())
                .item_id(proposeDto.getItem_id())
                .seller_id(proposeDto.getSeller_id())
                .buyer_id(proposeDto.getBuyer_id())
                .status(proposeDto.getStatus())
                .build();
    }

}
