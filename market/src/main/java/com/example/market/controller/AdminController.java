package com.example.market.controller;

import com.example.market.dto.MessageDto;
import com.example.market.dto.ShopDto;
import com.example.market.dto.ShopProposeDto;
import com.example.market.dto.UserDto;
import com.example.market.entity.ShopPropose;
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
    public ShopDto applyAdmit(
            @PathVariable("id") Long id){
        // 사업자 승인
        userService.approveBusinessApplication(id);
        // 승인과 동시에 쇼핑몰 개설됨
        return shopService.createShopForApprovedUser(id);
    }

    // 사업자 거절
    @PutMapping("/apply-reject/{id}")
    public UserDto applyReject(
            @PathVariable("id") Long id){

        return userService.rejectBusinessApplication(id);
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
        ShopPropose shopPropose = shopProposeService.searchById(propose_id);
        // 승인
        ShopProposeDto shopProposeDto = shopProposeService.approveShopProposal(propose_id);
        // 쇼핑몰은 오픈 상태로 바꾼다.
        shopService.changeShopStatus(shopPropose.getShopId(), Status.OPEN);

        return shopProposeDto;
    }

    // 쇼핑몰 거절 (이유 명시)
    @PutMapping("/reject/{propose_id}")
    public ShopProposeDto rejectShop(
            @PathVariable
            Long propose_id,
            @RequestBody
            MessageDto messageDto
    ){
        return shopProposeService.rejectShopProposal(propose_id, messageDto.getMessage());
    }

    // 쇼핑몰 폐쇄 요청 조회
    @GetMapping("/closing-shops")
    public List<ShopProposeDto> getClosingShops(){
        // 모든 쇼핑몰 요청을 조회
        return shopProposeService.searchAllByStatus(Status.CLOSING);
    }

    // 쇼핑몰 폐쇄
    @PutMapping("/closing-shops/{propose_id}")
    public ShopProposeDto closeShop(
            @PathVariable
            Long propose_id
    ){
        ShopPropose shopPropose = shopProposeService.searchById(propose_id);
        // 쇼핑몰 폐쇄 요청 수락 -> Closed
        ShopProposeDto shopProposeDto = shopProposeService.closeShop(propose_id);
        // 쇼핑몰 폐쇄
        shopService.changeShopStatus(shopPropose.getShopId(), Status.CLOSED);
        return shopProposeDto;
    }

}
