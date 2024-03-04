package com.example.market.service;

import com.example.market.dto.ShopProposeDto;
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
}
