package com.example.market.repo;

import com.example.market.entity.PurchasePropose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseProposeRepository extends JpaRepository<PurchasePropose, Long> {
    List<PurchasePropose> findAllByShopId(Long id);
}
