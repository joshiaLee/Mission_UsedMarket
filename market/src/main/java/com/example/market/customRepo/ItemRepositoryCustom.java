package com.example.market.customRepo;

import com.example.market.entity.Item;

import java.util.List;

public interface ItemRepositoryCustom {
    List<Item> findAllByNameAndPrice(String name, Integer above, Integer under);
}
