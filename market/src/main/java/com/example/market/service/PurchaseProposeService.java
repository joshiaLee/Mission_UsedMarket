package com.example.market.service;

import com.example.market.dto.PurchaseProposeDto;
import com.example.market.entity.PurchasePropose;
import com.example.market.enums.Status;
import com.example.market.repo.PurchaseProposeRepository;
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
public class PurchaseProposeService {
    private final PurchaseProposeRepository proposeRepository;

    @Transactional
    public PurchasePropose join(PurchasePropose propose){
        return proposeRepository.save(propose);
    }

    public PurchasePropose searchById(Long id){
        return proposeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    public List<PurchaseProposeDto> searchAllByShopId(Long id){
        return proposeRepository.findAllByShopId(id)
                .stream()
                .map(PurchaseProposeDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public PurchaseProposeDto changeStatus(PurchasePropose purchasePropose, Status status) {
        purchasePropose.setStatus(status);
        return PurchaseProposeDto.fromEntity(join(purchasePropose));

    }
}
