package com.example.market.repoImpl;

import com.example.market.customRepo.ItemRepositoryCustom;
import com.example.market.entity.Item;
import com.example.market.enums.Status;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.market.entity.QItem.*;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Item> findAllByNameAndPrice(String name, Integer above, Integer under) {
        return jpaQueryFactory.selectFrom(item)
                .where(
                        item.name.contains(name),
                        item.price.between(above, under),
                        item.status.eq(Status.SALE),
                        item.shop.id.isNotNull(),
                        item.shop.status.eq(Status.OPEN)
                )
                .orderBy(item.shop.recentTransaction.desc())
                .fetch();
    }
}
