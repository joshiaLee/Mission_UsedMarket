package com.example.market.service;

import com.example.market.entity.Shop;
import com.example.market.repo.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
}
