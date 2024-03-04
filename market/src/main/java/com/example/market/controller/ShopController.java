package com.example.market.controller;

import com.example.market.dto.MessageDto;
import com.example.market.dto.ShopDto;
import com.example.market.dto.ShopProposeDto;
import com.example.market.entity.Shop;
import com.example.market.entity.ShopPropose;
import com.example.market.entity.UserEntity;
import com.example.market.enums.Status;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.service.ShopProposeService;
import com.example.market.service.ShopService;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopController {

    private final AuthenticationFacade authFacade;
    private final ShopService shopService;
    private final UserService userService;


    private final ShopProposeService shopProposeService;
    // 쇼핑몰 수정
    @PutMapping("/update")
    public ShopDto updateShop(
            @RequestBody
            ShopDto shopDto
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = userService.searchByUsername(username);

        Shop shop = shopService.searchByUserEntityId((userEntity.getId()));

        shop.setName(shopDto.getName());
        shop.setIntroduction(shopDto.getIntroduction());
        shop.setCategory(shopDto.getCategory());


        return ShopDto.fromEntity(shopService.join(shop));

    }

    // 쇼핑몰 OPEN 요청
    @PutMapping("/apply-open")
    public ShopProposeDto openShop(

    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = userService.searchByUsername(username);
        Shop shop = shopService.searchByUserEntityId((userEntity.getId()));

        if(shop.getName() == null || shop.getIntroduction() == null || shop.getCategory() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쇼핑몰 정보가 부족합니다.");

        ShopPropose newPropose = ShopPropose.builder()
                .shopId(shop.getId())
                .status(Status.PROCEEDING)
                .build();

        return ShopProposeDto.fromEntity(shopProposeService.join(newPropose));

    }

    // 쇼핑몰 CLOSE 요청
    @PutMapping("/apply-close")
    private ShopProposeDto closeShop(
            @RequestBody
            MessageDto messageDto
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = userService.searchByUsername(username);
        Shop shop = shopService.searchByUserEntityId(userEntity.getId());
        if(!shop.getStatus().equals(Status.OPEN))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "오픈된 쇼핑몰만 폐쇄요청을 할수 있습니다.");

        ShopPropose newPropose = ShopPropose.builder()
                .shopId(shop.getId())
                .message(messageDto.getMessage())
                .status(Status.CLOSING)
                .build();

        return ShopProposeDto.fromEntity(shopProposeService.join(newPropose));
    }

    // 내 신청제안 보기
    @GetMapping("/my-proposes")
    public List<ShopProposeDto> getMyProposes(){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = userService.searchByUsername(username);
        Shop shop = shopService.searchByUserEntityId(userEntity.getId());

        return shopProposeService.searchAllByShopId(shop.getId());
    }
}
