package com.example.market.service;

import com.example.market.dto.PurchaseProposeDto;
import com.example.market.entity.PurchasePropose;
import com.example.market.repo.PurchaseProposeRepository;
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
public class PurchaseProposeService {
    private final PurchaseProposeRepository proposeRepository;

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
}
