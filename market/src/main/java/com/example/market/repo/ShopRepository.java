package com.example.market.repo;

import com.example.market.entity.Shop;
import com.example.market.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long>  {
    Optional<Shop> findByUserEntityId(Long id);

    List<Shop> findAllByOrderByRecentTransactionDesc();

    List<Shop> findAllByNameAndCategoryOrderByRecentTransactionDesc(String name, Category category);


}
