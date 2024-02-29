package com.example.market.article;

import com.example.market.article.dto.ArticleDto;
import com.example.market.article.entity.Article;
import com.example.market.article.repo.ArticleRepository;
import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleDto create(ArticleDto dto){
        UserEntity writer = getUserEntity();

        Article newArticle = Article.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(writer)
                .build();

        return ArticleDto.fromEntity(articleRepository.save(newArticle));
    }

    private UserEntity getUserEntity() {

        // 시큐리티 접속하는 UserDetails 뽑아옴
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
