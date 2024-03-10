package com.example.market.service;

import com.example.market.dto.ShopProposeDto;
import com.example.market.entity.ShopPropose;
import com.example.market.enums.Status;
import com.example.market.repo.ShopProposeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ShopProposeService {
    private final ShopProposeRepository shopProposeRepository;

    public ShopPropose searchById(Long id){
        return shopProposeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    public List<ShopProposeDto> searchAllByStatus(Status status){
        return shopProposeRepository
                .findAllByStatus(status)
                .stream()
                .map(ShopProposeDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShopProposeDto> searchAllByShopId(Long shopId){
        return shopProposeRepository
                .findAllByShopId(shopId)
                .stream()
                .map(ShopProposeDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShopPropose join(ShopPropose shopPropose){
        return shopProposeRepository.save(shopPropose);
    }

    // 쇼핑몰 승인
    @Transactional
    public ShopProposeDto approveShopProposal(Long proposeId) {
        ShopPropose shopPropose = searchById(proposeId);
        shopPropose.setStatus(Status.ADMITTED);

        return ShopProposeDto.fromEntity(join(shopPropose));
    }

    // 쇼핑몰 거절
    @Transactional
    public ShopProposeDto rejectShopProposal(Long proposeId, String message) {
        ShopPropose shopPropose = searchById(proposeId);
        shopPropose.setStatus(Status.REJECTED);
        shopPropose.setMessage(message);
        return ShopProposeDto.fromEntity(join(shopPropose));
    }

    // 쇼핑몰 폐쇄
    @Transactional
    public ShopProposeDto closeShop(Long proposeId) {
        ShopPropose shopPropose = searchById(proposeId);
        shopPropose.setStatus(Status.CLOSED);

        return ShopProposeDto.fromEntity(join(shopPropose));
    }



    public ShopProposeDto openPropose(Long id) {
        ShopPropose newPropose = ShopPropose.builder()
                .shopId(id)
                .status(Status.PROCEEDING)
                .build();

        return ShopProposeDto.fromEntity(join(newPropose));
    }

    public ShopProposeDto closePropose(Long id, String message) {
        ShopPropose newPropose = ShopPropose.builder()
                .shopId(id)
                .message(message)
                .status(Status.CLOSING)
                .build();
        return ShopProposeDto.fromEntity(join(newPropose));
    }
}
