package com.example.market.service;

import com.example.market.dto.ProposeDto;
import com.example.market.entity.Propose;
import com.example.market.enums.Status;
import com.example.market.repo.ProposeRepository;
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
public class ProposeService {
    private final ProposeRepository proposeRepository;

    @Transactional
    public ProposeDto completePropose(Propose proposeEntity){
        // 해당 제안만 승낙하고 나머지는 모두 거절로 변경
        List<Propose> RestOfPropose = searchAllByItemId(proposeEntity.getItemId());
        for (Propose propose : RestOfPropose) {
            if(propose.equals(proposeEntity))
                continue;

            propose.setStatus(Status.REJECTED);
            join(propose);
        }

        proposeEntity.setStatus(Status.COMPLETED);
        return ProposeDto.fromEntity(join(proposeEntity));
    }

    @Transactional
    public ProposeDto changeStatus(Propose propose, Status status){
        propose.setStatus(status);
        return ProposeDto.fromEntity(join(propose));
    }

    @Transactional
    public ProposeDto purchasePropose(Long item_id, Long seller_id, Long buyer_id){
        Propose propose = Propose.builder()
                .itemId(item_id)
                .sellerId(seller_id)
                .buyerId(buyer_id)
                .status(Status.PROPOSING)
                .build();

        return ProposeDto.fromEntity(join(propose));
    }

    @Transactional
    public Propose join(Propose propose){
        return proposeRepository.save(propose);
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
