package com.example.market.controller;

import com.example.market.dto.ItemDto;
import com.example.market.dto.ProposeDto;
import com.example.market.entity.Item;
import com.example.market.entity.Propose;
import com.example.market.entity.UserEntity;
import com.example.market.enums.Status;
import com.example.market.facade.AuthenticationFacade;
import com.example.market.service.ItemService;
import com.example.market.service.ProposeService;
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
@RequestMapping("/propose")
public class ProposeController {

    private final UserService service;
    private final ItemService itemService;
    private final ProposeService proposeService;
    private final AuthenticationFacade authFacade;

    // 구매 제안
    @PutMapping("/{item_id}")
    public ItemDto proposeItem(
            @PathVariable("item_id")
            Long item_id
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);

        Item item = itemService.searchById(item_id);
        Long seller_id = item.getUserEntity().getId();

        if(userEntity.getId().equals(seller_id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "본인 상품을 구매할수 없습니다.");

        Propose propose = Propose.builder()
                .itemId(item_id)
                .sellerId(seller_id)
                .buyerId(userEntity.getId())
                .status(Status.PROPOSING)
                .build();

        proposeService.join(propose);

        return ItemDto.fromEntity(item);

    }

    // 구매 제안 받은 목록
    @GetMapping("/received-list")
    public List<ProposeDto> getReceivedList(){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);

        return proposeService.searchAllBySellerId(userEntity.getId());

    }

    // 구매 제안 보낸 목록
    @GetMapping("/sent-list")
    public List<ProposeDto> getSentList(){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);

        return proposeService.searchAllByBuyerId(userEntity.getId());

    }

    // 구매 제안 승인
    @PutMapping("/admit/{propose_id}")
    public String admitPropose(
            @PathVariable("propose_id")
            Long propose_id
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);
        Propose proposeEntity = proposeService.searchById(propose_id);

        if(!userEntity.getId().equals(proposeEntity.getSellerId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 요청만 승낙할수 있습니다,");

        proposeEntity.setStatus(Status.ADMITTED);

        proposeService.join(proposeEntity);

        return "propose admitted";
    }

    // 구매 제안 거절
    @PutMapping("/reject/{propose_id}")
    public String rejectPropose(
            @PathVariable("propose_id")
            Long propose_id
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);
        Propose proposeEntity = proposeService.searchById(propose_id);

        if(!userEntity.getId().equals(proposeEntity.getSellerId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 요청만 거절할수 있습니다,");

        proposeEntity.setStatus(Status.REJECTED);

        proposeService.join(proposeEntity);

        return "propose rejected";
    }

    // 구매 제안 물건중 승낙된 물건 조회
    @GetMapping("/admit-list")
    public List<ProposeDto> admitList(){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);
        return proposeService.searchAllAdmitted(userEntity.getId(), Status.ADMITTED);

    }

    // 승낙된 물건 구매확정
    @PutMapping("/admit-list/{propose_id}")
    public String confirmPropose(
            @PathVariable("propose_id")
            Long propose_id
    ){
        String username = authFacade.getAuth().getName();
        UserEntity userEntity = service.searchByUsername(username);
        Propose proposeEntity = proposeService.searchById(propose_id);

        if(!userEntity.getId().equals(proposeEntity.getBuyerId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 내역만 구매확정 할수 있습니다,");

        // 해당 제안만 승낙하고 나머지는 모두 거절로 변경
        List<Propose> RestOfPropose = proposeService.searchAllByItemId(proposeEntity.getItemId());
        for (Propose propose : RestOfPropose) {
            if(propose.equals(proposeEntity))
                continue;

            propose.setStatus(Status.REJECTED);
            proposeService.join(propose);
        }

        proposeEntity.setStatus(Status.COMPLETED);
        proposeService.join(proposeEntity);

        return "propose successfully confirmed";

    }
}
