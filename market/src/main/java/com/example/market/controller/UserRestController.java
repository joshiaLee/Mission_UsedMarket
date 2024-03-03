package com.example.market.controller;

import com.example.market.dto.ImageDto;
import com.example.market.dto.ItemDto;
import com.example.market.dto.UpdateItemResponse;
import com.example.market.entity.ImageEntity;
import com.example.market.entity.Item;
import com.example.market.entity.UserEntity;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.repo.ImageRepository;
import com.example.market.service.ItemService;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserRestController {

    private final ItemService itemService;
    private final UserService userService;
    private final ImageRepository imageRepository;
    private final AuthenticationFacade authFacade;

    @GetMapping("/item-list")
    public List<ItemDto> viewItemList(){
        return itemService.findAllItem();
    }

    @GetMapping("/my-items")
    public List<ItemDto> myItemList(){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = userService.searchByUsername(username);

        return itemService.findAllItemByUser_Id(userEntity.getId());
    }

    @DeleteMapping("/delete-item/{item_id}")
    public String deleteItem(
            @PathVariable("item_id")
            Long item_id
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = userService.searchByUsername(username);

        Item item = itemService.searchById(item_id);

        // 어드민이 아닌데 접속자의 id 와 아이템의 주인의 id 가 다르다면 FORBIDDEN
        if(!userEntity.getAuthorities().equals("ROLE_ADMIN") && item.getUserEntity().getId() != userEntity.getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        itemService.deleteItem(item_id);


        return "item's successfully deleted";
    }

    @GetMapping("/update-view/{item_id}")
    public UpdateItemResponse updateView(
            @PathVariable("item_id")
            Long item_id
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = userService.searchByUsername(username);

        Item item = itemService.searchById(item_id);

        if(!userEntity.getAuthorities().equals("ROLE_ADMIN") && item.getUserEntity().getId() != userEntity.getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);



        ItemDto itemDto = ItemDto.fromEntity(item);
        List<ImageEntity> imageEntities = imageRepository.findAllByItemId(item_id);
        List<ImageDto> imagesDto = imageEntities.stream().map(ImageDto::fromEntity).collect(Collectors.toList());

        return new UpdateItemResponse(itemDto, imagesDto);
    }



}
