package com.example.market.repo;

import com.example.market.entity.Propose;
import com.example.market.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposeRepository extends JpaRepository<Propose, Long> {
    List<Propose> findAllBySellerId(Long sellerId);
    List<Propose> findAllByBuyerId(Long buyerId);

    List<Propose> findAllByBuyerIdAndStatus(Long buyerId, Status status);

    List<Propose> findAllByItemId(Long itemId);
}
