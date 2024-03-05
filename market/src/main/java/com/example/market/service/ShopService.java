package com.example.market.service;

import com.example.market.dto.ShopDto;
import com.example.market.entity.Shop;
import com.example.market.enums.Category;
import com.example.market.enums.Status;
import com.example.market.repo.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShopService {
    private final ShopRepository shopRepository;

    public Shop join(Shop shop){
        return shopRepository.save(shop);
    }
    public Shop searchById(Long id){
        return shopRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    public Shop searchByUserEntityId(Long id){
        return shopRepository.findByUserEntityId(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    public List<ShopDto> searchAll(){
        return shopRepository
                .findAllByOrderByRecentTransactionDesc()
                .stream()
                .map(ShopDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShopDto> searchAllByNameAndCategory(String name, Category category, Status status){
        return shopRepository
                .findAllByNameContainingAndCategoryAndStatusOrderByRecentTransactionDesc(name, category, status)
                .stream()
                .map(ShopDto::fromEntity)
                .collect(Collectors.toList());
    }
}
