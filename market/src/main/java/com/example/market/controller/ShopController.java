package com.example.market.controller;

import com.example.market.dto.*;
import com.example.market.entity.*;
import com.example.market.enums.Status;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopController {

    private final AuthenticationFacade authFacade;
    private final ShopService shopService;
    private final ItemService itemService;
    private final PurchaseProposeService proposeService;
    private final PurchaseProposeService purchaseProposeService;
    private final ImageService imageService;


    private final ShopProposeService shopProposeService;
    // 쇼핑몰 수정
    @PutMapping("/update")
    public ShopDto updateShop(
            @RequestBody
            ShopDto shopDto
    ){
        UserEntity userEntity = authFacade.getUserEntity();
        Shop shop = shopService.searchByUserEntityId((userEntity.getId()));

        return shopService.update(shop, shopDto);
    }

    // 쇼핑몰 OPEN 요청
    @PutMapping("/apply-open")
    public ShopProposeDto openShop(

    ){
        UserEntity userEntity = authFacade.getUserEntity();
        Shop shop = shopService.searchByUserEntityId((userEntity.getId()));

        if(shop.getName() == null || shop.getIntroduction() == null || shop.getCategory() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "쇼핑몰 정보가 부족합니다.");
        if(shop.getStatus().equals(Status.OPEN))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 오픈된 쇼핑몰입니다.");

        return shopProposeService.openPropose(shop.getId());

    }

    // 쇼핑몰 CLOSE 요청
    @PutMapping("/apply-close")
    private ShopProposeDto closeShop(
            @RequestBody
            MessageDto messageDto
    ){
        UserEntity userEntity = authFacade.getUserEntity();
        Shop shop = shopService.searchByUserEntityId(userEntity.getId());
        if(!shop.getStatus().equals(Status.OPEN))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "오픈된 쇼핑몰만 폐쇄요청을 할수 있습니다.");


        return shopProposeService.closePropose(shop.getId(), messageDto.getMessage());
    }

    // 내 신청제안 보기
    @GetMapping("/my-proposes")
    public List<ShopProposeDto> getMyProposes(){
        UserEntity userEntity = authFacade.getUserEntity();
        Shop shop = shopService.searchByUserEntityId(userEntity.getId());

        return shopProposeService.searchAllByShopId(shop.getId());
    }

    // 상점 아이템 추가
    @PostMapping("/add-item")
    public ItemDto addItem(
            MultipartFile[] files,
            @ModelAttribute
            ItemDto itemDto
    ) throws IOException {
        if (files.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지가 한장이상 있어야 합니다.");
        }


        UserEntity userEntity = authFacade.getUserEntity();
        Shop shop = shopService.searchByUserEntityId(userEntity.getId());

        Item savedItem = itemService.addShopItem(itemDto, shop);

        imageService.addImages(files, savedItem);

        return ItemDto.fromEntity(savedItem);

    }
    
    // 쇼핑몰 구매제안 확인
    @GetMapping("/purchases-list")
    public List<PurchaseProposeDto> PurchaseList(){
        UserEntity userEntity = authFacade.getUserEntity();
        Shop shop = shopService.searchByUserEntityId(userEntity.getId());
        
        return proposeService.searchAllByShopId(shop.getId());
    }
    
    // 구매제안 승낙 -> 아이템 재고 감소
    @PutMapping("/admit-purchase/{purchase_id}")
    public PurchaseProposeDto admitPurchase(
            @PathVariable
            Long purchase_id
    ){
        UserEntity userEntity = authFacade.getUserEntity();
        Shop shop = shopService.searchByUserEntityId(userEntity.getId());
        PurchasePropose purchasePropose = proposeService.searchById(purchase_id);

        // 해당 상점이 아니거나 구매제안의 Status 가 PROCEEDING 이 아닌 경우
        if(!shop.getId().equals(purchasePropose.getShopId()) || !purchasePropose.getStatus().equals(Status.PROCEEDING))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        
        // Transaction 로직 실행
        itemService.purchaseItem(purchasePropose.getItemId(), purchasePropose.getQuantity());

        // 구매 시간 갱신
        shopService.changeRecent(shop);

        // 구매 제안 최종 승인
        return purchaseProposeService.changeStatus(purchasePropose, Status.ADMITTED);
        
    }

    // 구매제안 거절
    @PutMapping("/reject-purchase/{purchase_id}")
    public PurchaseProposeDto rejectPropose(
            @PathVariable("purchase_id")
            Long purchase_id
    ){
        UserEntity userEntity = authFacade.getUserEntity();
        Shop shop = shopService.searchByUserEntityId(userEntity.getId());
        PurchasePropose purchasePropose = proposeService.searchById(purchase_id);

        if(!shop.getId().equals(purchasePropose.getShopId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인상점의 물건만 거절할수 있습니다.");

        return purchaseProposeService.changeStatus(purchasePropose, Status.REJECTED);
    }
}
