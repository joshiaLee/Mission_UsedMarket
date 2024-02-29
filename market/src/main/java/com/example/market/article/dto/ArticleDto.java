package com.example.market.article.dto;

import com.example.market.article.entity.Article;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    private Long id;
    @Setter
    private String title;
    @Setter
    private String content;
    @Setter
    private String writer;

    public static ArticleDto fromEntity(Article entity){
        return ArticleDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter().getUsername())
                .build();
    }

}
