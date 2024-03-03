package com.example.market.controller;

import com.example.market.dto.ItemDto;
import com.example.market.dto.ProposeDto;
import com.example.market.entity.Item;
import com.example.market.entity.Propose;
import com.example.market.entity.UserEntity;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.repo.ProposeRepository;
import com.example.market.service.ItemService;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/propose")
public class ProposeController {

    private final UserService service;
    private final ItemService itemService;
    private final ProposeRepository proposeRepository;
    private final AuthenticationFacade authFacade;
    @GetMapping("/{item_id}")
    public ItemDto proposeItem(
            @PathVariable("item_id")
            Long item_id
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);

        Item item = itemService.searchById(item_id);
        Long seller_id = item.getUserEntity().getId();

        if(userEntity.getId() == seller_id)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인 상품을 구매할수 없습니다.");

        Propose propose = Propose.builder()
                .itemId(item_id)
                .sellerId(seller_id)
                .buyerId(userEntity.getId())
                .status("Proposing")
                .build();

        proposeRepository.save(propose);

        return ItemDto.fromEntity(item);

    }

    @GetMapping("/received-list")
    public List<ProposeDto> getReceivedList(){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);

        List<Propose> receivedList = proposeRepository.findAllBySellerId(userEntity.getId());

        return receivedList.stream().map(ProposeDto::fromEntity).collect(Collectors.toList());
    }

    @GetMapping("/sent-list")
    public List<ProposeDto> getSentList(){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);

        List<Propose> sentList = proposeRepository.findAllByBuyerId(userEntity.getId());

        return sentList.stream().map(ProposeDto::fromEntity).collect(Collectors.toList());
    }



}
