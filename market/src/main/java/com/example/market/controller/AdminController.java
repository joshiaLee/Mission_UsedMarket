package com.example.market.controller;

import com.example.market.dto.MessageDto;
import com.example.market.dto.ShopDto;
import com.example.market.dto.ShopProposeDto;
import com.example.market.dto.UserDto;
import com.example.market.entity.Shop;
import com.example.market.entity.ShopPropose;
import com.example.market.entity.UserEntity;
import com.example.market.enums.Status;
import com.example.market.service.ShopProposeService;
import com.example.market.service.ShopService;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminController {
    private final UserService userService;
    private final ShopService shopService;
    private final ShopProposeService shopProposeService;
    // 사업자 신청 목록
    @GetMapping("/apply-list")
    public List<UserDto> applyList(){
        return userService.userEntitySearchByAuthoritiesAndStatus("ROLE_USER", Status.PROCEEDING);
    }

    // 사업자 승인
    @PutMapping("/apply-admit/{id}")
    public String applyAdmit(
            @PathVariable("id") Long id){


        UserEntity userEntity = userService.searchById(id);

        userEntity.setAuthorities("ROLE_CEO");
        userEntity.setStatus(Status.ADMITTED);

        // 사업자 승인과 동시에 준비중인 쇼핑몰 개설
        Shop newShop = Shop.builder()
                .userEntity(userEntity)
                .status(Status.PREPARING)
                .build();

        shopService.join(newShop);


        userService.updateUser(userEntity);

        return "admitted";
    }

    // 사업자 거절
    @PutMapping("/apply-reject/{id}")
    public String applyReject(
            @PathVariable("id") Long id){

        UserEntity userEntity = userService.searchById(id);

        userEntity.setStatus(Status.REJECTED);
        userService.updateUser(userEntity);

        return "rejected";
    }

    // 쇼핑몰 신청 리스트 확인
    @GetMapping("/shops-list")
    public List<ShopProposeDto> getShopsList(){
        // 모든 쇼핑몰 요청을 조회
        return shopProposeService.searchAllByStatus(Status.PROCEEDING);
    }

    // 쇼핑몰 내용 확인 (허가 할지 거절 할지 판단하기위함)
    @GetMapping("/shops/{shop_id}")
    public ShopDto getShop(
            @PathVariable
            Long shop_id
    ){
        return ShopDto.fromEntity(shopService.searchById(shop_id));
    }

    // 쇼핑몰 허가

    @PutMapping("/admit/{propose_id}")
    public ShopProposeDto admitShop(
            @PathVariable
            Long propose_id
    ){

        // Propose 승낙
        ShopPropose shopPropose = shopProposeService.searchById(propose_id);
        shopPropose.setStatus(Status.ADMITTED);

        Long shop_id = shopPropose.getShopId();

        // Shop 오픈
        Shop shop = shopService.searchById(shop_id);
        shop.setStatus(Status.OPEN);

        shopService.join(shop);



        return ShopProposeDto.fromEntity(shopProposeService.join(shopPropose));
    }

    // 쇼핑몰 거절 (이유 명시)
    @PutMapping("/reject/{propose_id}")
    public ShopProposeDto rejectShop(
            @PathVariable
            Long propose_id,
            @RequestBody
            MessageDto messageDto
    ){
        ShopPropose shopPropose = shopProposeService.searchById(propose_id);

        shopPropose.setStatus(Status.REJECTED);
        shopPropose.setMessage(messageDto.getMessage());

        return ShopProposeDto.fromEntity(shopProposeService.join(shopPropose));
    }

    // 쇼핑몰 폐쇄 요청 조회
    @GetMapping("/closing-shops")
    public List<ShopProposeDto> getClosingShops(){
        // 모든 쇼핑몰 요청을 조회
        return shopProposeService.searchAllByStatus(Status.CLOSING);
    }

    // 쇼핑몰 폐쇄
    @PutMapping("/closing-shops/{propose_id}")
    public ShopDto closeShop(
            @PathVariable
            Long propose_id
    ){
        ShopPropose shopPropose = shopProposeService.searchById(propose_id);
        shopPropose.setStatus(Status.ADMITTED);
        shopProposeService.join(shopPropose);

        Shop shop = shopService.searchById(shopPropose.getShopId());
        shop.setStatus(Status.CLOSED);

        return ShopDto.fromEntity(shopService.join(shop));
    }




}
