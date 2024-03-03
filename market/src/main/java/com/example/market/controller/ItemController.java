package com.example.market.controller;

import com.example.market.dto.ImageDto;
import com.example.market.dto.ItemDto;
import com.example.market.dto.UpdateItemResponse;
import com.example.market.entity.ImageEntity;
import com.example.market.entity.Item;
import com.example.market.entity.Propose;
import com.example.market.entity.UserEntity;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.facade.ImageFacade;
import com.example.market.repo.ImageRepository;
import com.example.market.repo.ProposeRepository;
import com.example.market.service.ItemService;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class ItemController {

    private final ItemService itemService;
    private final UserService service;
    private final ImageRepository imageRepository;
    private final ProposeRepository proposeRepository;

    private final AuthenticationFacade authFacade;



    @PostMapping("/add-item")
    public String addItem(
            MultipartFile[] files,
            @ModelAttribute
            ItemDto itemDto,
            Authentication authentication
    ) throws IOException {
        Item item = Item.fromDto(itemDto);
        log.info(itemDto.toString());

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        UserEntity userEntity = service.searchByUsername(username);


        item.setUserEntity(userEntity);
        item.setStatus("OnSale");

        itemService.join(item);

        if (files != null && files.length > 0){
            for(MultipartFile file: files) {
                ImageEntity imageEntity = ImageFacade.AssociatedImage(item, file);
                imageRepository.save(imageEntity);
            }
        }

        return "ok";
    }


    @GetMapping("/item-list")
    public List<ItemDto> viewItemList(){
        return itemService.findAllItem();
    }

    @GetMapping("/my-items")
    public List<ItemDto> myItemList(){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);

        return itemService.findAllItemByUser_Id(userEntity.getId());
    }

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

    @GetMapping("/update-view/{item_id}")
    public UpdateItemResponse updateView(
            @PathVariable("item_id")
            Long item_id
    ){
        Item item = itemService.searchById(item_id);

        checkUserAuthorizationForItem(item);


        ItemDto itemDto = ItemDto.fromEntity(item);
        List<ImageEntity> imageEntities = imageRepository.findAllByItemId(item_id);
        List<ImageDto> imagesDto = imageEntities.stream().map(ImageDto::fromEntity).collect(Collectors.toList());

        return new UpdateItemResponse(itemDto, imagesDto);
    }



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

        item.setName(itemDto.getName());
        item.setContent(itemDto.getContent());
        item.setPrice(itemDto.getPrice());

        itemService.join(item);

        if (files != null && files.length > 0){
            for(MultipartFile file: files) {
                ImageEntity imageEntity = ImageFacade.AssociatedImage(item, file);
                imageRepository.save(imageEntity);
            }
        }

        return ItemDto.fromEntity(item);
    }

    @DeleteMapping("/delete-item-image/{image_id}")
    public String deleteItemImage(
            @PathVariable("image_id")
            Long image_id
    ){
        ImageEntity imageEntity = imageRepository.findById(image_id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 이미지가 없습니다.")
        );

        Item item = imageEntity.getItem();

        checkUserAuthorizationForItem(item);

        imageRepository.deleteById(image_id);

        return "image successfully deleted";
    }






    // 어드민이 아닌데 삭제를 할경우 아이템의 주인 인지 확인
    private void checkUserAuthorizationForItem(Item item) {
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);


        if(!userEntity.getAuthorities().equals("ROLE_ADMIN") && item.getUserEntity().getId() != userEntity.getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }


}
