package com.example.market.controller;

import com.example.market.dto.ProposeDto;
import com.example.market.entity.Item;
import com.example.market.entity.Propose;
import com.example.market.entity.UserEntity;
import com.example.market.enums.Status;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.service.ItemService;
import com.example.market.service.ProposeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/propose")
public class ProposeController {

    private final ItemService itemService;
    private final ProposeService proposeService;
    private final AuthenticationFacade authFacade;

    // 구매 제안
    @PutMapping("/{item_id}")
    public ProposeDto proposeItem(
            @PathVariable("item_id")
            Long item_id
    ){
        UserEntity userEntity = authFacade.getUserEntity();

        Item item = itemService.searchById(item_id);
        Long seller_id = item.getUserEntity().getId();

        if(userEntity.getId().equals(seller_id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인 상품을 구매할수 없습니다.");


        return proposeService.purchasePropose(item_id, seller_id, userEntity.getId());
    }

    // 구매 제안 받은 목록
    @GetMapping("/received-list")
    public List<ProposeDto> getReceivedList(){
        UserEntity userEntity = authFacade.getUserEntity();

        return proposeService.searchAllBySellerId(userEntity.getId());

    }

    // 구매 제안 보낸 목록
    @GetMapping("/sent-list")
    public List<ProposeDto> getSentList(){
        UserEntity userEntity = authFacade.getUserEntity();

        return proposeService.searchAllByBuyerId(userEntity.getId());

    }

    // 구매 제안 승인
    @PutMapping("/admit/{propose_id}")
    public ProposeDto admitPropose(
            @PathVariable("propose_id")
            Long propose_id
    ){
        UserEntity userEntity = authFacade.getUserEntity();
        Propose proposeEntity = proposeService.searchById(propose_id);

        if(!userEntity.getId().equals(proposeEntity.getSellerId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 요청만 승낙할수 있습니다,");



        return proposeService.changeStatus(proposeEntity, Status.ADMITTED);
    }

    // 구매 제안 거절
    @PutMapping("/reject/{propose_id}")
    public ProposeDto rejectPropose(
            @PathVariable("propose_id")
            Long propose_id
    ){
        UserEntity userEntity = authFacade.getUserEntity();
        Propose proposeEntity = proposeService.searchById(propose_id);

        if(!userEntity.getId().equals(proposeEntity.getSellerId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 요청만 거절할수 있습니다,");

        return proposeService.changeStatus(proposeEntity, Status.REJECTED);
    }

    // 구매 제안 물건중 승낙된 물건 조회
    @GetMapping("/admit-list")
    public List<ProposeDto> admitList(){
        UserEntity userEntity = authFacade.getUserEntity();
        return proposeService.searchAllAdmitted(userEntity.getId(), Status.ADMITTED);

    }

    // 승낙된 물건 구매확정
    @PutMapping("/admit-list/{propose_id}")
    public ProposeDto confirmPropose(
            @PathVariable("propose_id")
            Long propose_id
    ){
        UserEntity userEntity = authFacade.getUserEntity();
        Propose proposeEntity = proposeService.searchById(propose_id);

        if(!userEntity.getId().equals(proposeEntity.getBuyerId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 내역만 구매확정 할수 있습니다,");


        // 구매 확정
        return proposeService.completePropose(proposeEntity);

    }
}
