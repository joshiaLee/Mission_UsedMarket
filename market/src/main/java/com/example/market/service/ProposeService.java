package com.example.market.service;

import com.example.market.dto.ProposeDto;
import com.example.market.entity.Propose;
import com.example.market.enums.Status;
import com.example.market.repo.ProposeRepository;
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
public class ProposeService {
    private final ProposeRepository proposeRepository;

    public void join(Propose propose){
        proposeRepository.save(propose);
    }

    public Propose searchById(Long id){
        return proposeRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    public List<ProposeDto> searchAllBySellerId(Long sellerId){
        return proposeRepository
                .findAllBySellerId(sellerId)
                .stream()
                .map(ProposeDto::fromEntity)
                .collect(Collectors.toList());
    }
    public List<ProposeDto> searchAllByBuyerId(Long buyerId){
        return proposeRepository
                .findAllByBuyerId(buyerId)
                .stream()
                .map(ProposeDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProposeDto> searchAllAdmitted(Long buyerId, Status status){
        return proposeRepository
                .findAllByBuyerIdAndStatus(buyerId, status)
                .stream()
                .map(ProposeDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<Propose> searchAllByItemId(Long itemId){
        return proposeRepository.findAllByItemId(itemId);
    }

}
