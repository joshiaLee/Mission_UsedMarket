package com.example.market.repo;

import com.example.market.entity.ShopPropose;
import com.example.market.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopProposeRepository extends JpaRepository<ShopPropose, Long> {
    List<ShopPropose> findAllByStatus(Status status);

    List<ShopPropose> findAllByShopId(Long id);
}
