package com.example.market.controller;

import com.example.market.dto.ShopDto;
import com.example.market.dto.ShopProposeDto;
import com.example.market.entity.Shop;
import com.example.market.entity.ShopPropose;
import com.example.market.entity.UserEntity;
import com.example.market.enums.Status;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.repo.ShopProposeRepository;
import com.example.market.service.ShopService;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class ShopController {

    private final AuthenticationFacade authFacade;
    private final ShopService shopService;
    private final UserService userService;

    private final ShopProposeRepository shopProposeRepository;

    // 쇼핑몰 수
    @PutMapping("/update")
    public ShopDto updateShop(
            @RequestBody
            ShopDto shopDto
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = userService.searchByUsername(username);

        Shop shop = shopService.searchById(userEntity.getId());

        shop.setName(shopDto.getName());
        shop.setIntroduction(shopDto.getIntroduction());
        shop.setCategory(shopDto.getCategory());


        return ShopDto.fromEntity(shopService.join(shop));

    }

    // 쇼핑몰 OPEN 요청
    @PutMapping("/apply")
    public ShopProposeDto updateShop(

    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = userService.searchByUsername(username);
        Shop shop = shopService.searchById(userEntity.getId());

        if(shop.getName() == null || shop.getIntroduction() == null || shop.getCategory() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쇼핑몰 정보가 부족합니다.");

        ShopPropose newPropose = ShopPropose.builder()
                .shopId(shop.getId())
                .status(Status.PROCEEDING)
                .build();

        return ShopProposeDto.fromEntity(shopProposeRepository.save(newPropose));

    }
}
