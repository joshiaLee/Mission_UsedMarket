package com.example.market.repo;

import com.example.market.customRepo.ItemRepositoryCustom;
import com.example.market.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    List<Item> findAllByUserEntityId(Long userId);
}
