package com.example.market.controller;

import com.example.market.dto.ImageDto;
import com.example.market.dto.ItemDto;
import com.example.market.dto.UpdateItemResponse;
import com.example.market.entity.ImageEntity;
import com.example.market.entity.Item;
import com.example.market.entity.UserEntity;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.facade.ImageFacade;
import com.example.market.repo.ImageRepository;
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

        return "redirect:/users/home";
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
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);

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
        UserEntity userEntity = service.searchByUsername(username);

        Item item = itemService.searchById(item_id);

        if(!userEntity.getAuthorities().equals("ROLE_ADMIN") && item.getUserEntity().getId() != userEntity.getId())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);



        ItemDto itemDto = ItemDto.fromEntity(item);
        List<ImageEntity> imageEntities = imageRepository.findAllByItemId(item_id);
        List<ImageDto> imagesDto = imageEntities.stream().map(ImageDto::fromEntity).collect(Collectors.toList());

        return new UpdateItemResponse(itemDto, imagesDto);
    }



//    @PutMapping("/update-item/{item_id}")



}
