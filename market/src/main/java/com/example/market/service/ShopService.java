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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ShopService {
    private final ShopRepository shopRepository;
    private final UserService userService;


    @Transactional
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

    // Shop 개설 로직
    @Transactional
    public ShopDto createShopForApprovedUser(Long userId) {
        Shop newShop = Shop.builder()
                .userEntity(userService.searchById(userId))
                .status(Status.PREPARING)
                .build();
        return ShopDto.fromEntity(join(newShop));
    }

    // Shop의 상태 변경
    @Transactional
    public ShopDto changeShopStatus(Long shop_id, Status status){
        Shop shop = searchById(shop_id);
        shop.setStatus(status);
        return ShopDto.fromEntity(join(shop));
    }

    public ShopDto update(Shop shop, ShopDto shopDto) {
        shop.setName(shopDto.getName());
        shop.setIntroduction(shopDto.getIntroduction());
        shop.setCategory(shopDto.getCategory());
        return ShopDto.fromEntity(join(shop));
    }

    public void changeRecent(Shop shop) {
        LocalDateTime now = LocalDateTime.now();
        shop.setRecentTransaction(now);
        join(shop);
    }
}
