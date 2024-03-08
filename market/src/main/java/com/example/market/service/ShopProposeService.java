package com.example.market.service;

import com.example.market.dto.ShopProposeDto;
import com.example.market.entity.Shop;
import com.example.market.entity.ShopPropose;
import com.example.market.enums.Status;
import com.example.market.repo.ShopProposeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ShopProposeService {
    private final ShopProposeRepository shopProposeRepository;
    private final ShopService shopService;

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

    public ShopPropose join(ShopPropose shopPropose){
        return shopProposeRepository.save(shopPropose);
    }

    // 쇼핑몰 승인
    public ShopProposeDto approveShopProposal(Long proposeId) {
        ShopPropose shopPropose = searchById(proposeId);
        shopPropose.setStatus(Status.ADMITTED);
        Shop shop = shopService.searchById(shopPropose.getShopId()); // shopService는 ShopProposeService 내에 주입되어야 함
        shop.setStatus(Status.OPEN);
        shopService.join(shop); // 상태 업데이트
        return ShopProposeDto.fromEntity(join(shopPropose));
    }

    // 쇼핑몰 거절
    public ShopProposeDto rejectShopProposal(Long proposeId, String message) {
        ShopPropose shopPropose = searchById(proposeId);
        shopPropose.setStatus(Status.REJECTED);
        shopPropose.setMessage(message);
        return ShopProposeDto.fromEntity(join(shopPropose));
    }

    public ShopProposeDto closeShop(Long proposeId) {
        ShopPropose shopPropose = searchById(proposeId);
        shopPropose.setStatus(Status.CLOSED);

        Shop shop = shopService.searchById(shopPropose.getShopId());
        shop.setStatus(Status.CLOSED);
        shopService.join(shop);

        return ShopProposeDto.fromEntity(join(shopPropose));
    }
}
