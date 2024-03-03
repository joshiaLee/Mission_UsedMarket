package com.example.market.repo;

import com.example.market.entity.Propose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProposeRepository extends JpaRepository<Propose, Long> {
    List<Propose> findAllBySellerId(Long seller_id);
    List<Propose> findAllByBuyerId(Long buyer_id);
}
