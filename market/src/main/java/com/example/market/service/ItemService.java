package com.example.market.service;

import com.example.market.dto.ItemDto;
import com.example.market.entity.Item;
import com.example.market.repo.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Item join(Item item){
        return itemRepository.save(item);
    }



    public List<ItemDto> findAllItem(){
        return itemRepository
                .findAll()
                .stream()
                .map(ItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ItemDto> findAllItemByUserId(Long user_id){
        return itemRepository
                .findAllByUserEntityId(user_id)
                .stream()
                .map(ItemDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Item searchById(Long id){
        return itemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }

    public void deleteItem(Long id){
        itemRepository.deleteById(id);
    }

    public List<Item> searchAllByNameAndPrice(String name, Integer above, Integer under){
        return itemRepository.findAllByNameAndPrice(name, above, under);
    }
}
