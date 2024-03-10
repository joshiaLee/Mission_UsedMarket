package com.example.market.controller;

import com.example.market.dto.ImageDto;
import com.example.market.dto.ItemDto;
import com.example.market.dto.UpdateItemResponseDto;
import com.example.market.entity.ImageEntity;
import com.example.market.entity.Item;
import com.example.market.entity.UserEntity;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.service.ImageService;
import com.example.market.service.ItemService;
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
@RequestMapping("/users")
public class ItemController {

    private final ItemService itemService;
    private final ImageService imageService;

    private final AuthenticationFacade authFacade;



    // 새로운 아이템 등록
    @PostMapping("/add-item")
    public ItemDto addItem(
            MultipartFile[] files,
            @ModelAttribute
            ItemDto itemDto
    ) throws IOException {

        UserEntity userEntity = authFacade.getUserEntity();

        // 아이템 등록
        Item savedItem = itemService.addItem(itemDto, userEntity);

        // 이미지 등록
        imageService.addImages(files, savedItem);

        return ItemDto.fromEntity(savedItem);
    }


    // 아이템 목록 조회
    @GetMapping("/item-list")
    public List<ItemDto> viewItemList(){
        return itemService.findAllItem();
    }

    // 내 아이템 조회
    @GetMapping("/my-items")
    public List<ItemDto> myItemList(){

        UserEntity userEntity = authFacade.getUserEntity();

        return itemService.findAllItemByUserId(userEntity.getId());
    }

    // 내 아이템 삭제
    @DeleteMapping("/delete-item/{item_id}")
    public String deleteItem(
            @PathVariable("item_id")
            Long item_id
    ){
        Item item = itemService.searchById(item_id);

        checkUserAuthorizationForItem(item);

        itemService.deleteItem(item_id);


        return "item's successfully deleted";
    }

    // 아이템 업데이트 화면
    @GetMapping("/update-view/{item_id}")
    public UpdateItemResponseDto updateView(
            @PathVariable("item_id")
            Long item_id
    ){
        Item item = itemService.searchById(item_id);

        checkUserAuthorizationForItem(item);


        ItemDto itemDto = ItemDto.fromEntity(item);
        List<ImageDto> imageDtoS = imageService.searchAllImagesByItemId(item_id);

        return new UpdateItemResponseDto(itemDto, imageDtoS);
    }



    // 아이템 업데이트
    @PutMapping("/update-item/{item_id}")
    public ItemDto updateItem(
            @PathVariable("item_id")
            Long item_id,
            MultipartFile[] files,
            @ModelAttribute
            ItemDto itemDto
    ) throws IOException {
        Item item = itemService.searchById(item_id);

        checkUserAuthorizationForItem(item);

        Item savedItem = itemService.updateItem(item, itemDto);

        imageService.addImages(files, savedItem);

        return ItemDto.fromEntity(savedItem);
    }

    // 아이템 사진 개별 지우기
    @DeleteMapping("/delete-item-image/{image_id}")
    public String deleteItemImage(
            @PathVariable("image_id")
            Long image_id
    ){

        ImageEntity imageEntity = imageService.searchById(image_id);

        checkUserAuthorizationForItem(imageEntity.getItem());

        imageService.deleteImage(image_id);

        return "image successfully deleted";
    }






    // 어드민이 아닌데 삭제를 할경우 아이템의 주인 인지 확인
    private void checkUserAuthorizationForItem(Item item) {
        UserEntity userEntity = authFacade.getUserEntity();

        if(!userEntity.getAuthorities().equals("ROLE_ADMIN") && !item.getUserEntity().getId().equals(userEntity.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }


}
