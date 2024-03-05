package com.example.market;

import com.example.market.entity.Shop;
import com.example.market.enums.Status;
import com.example.market.repo.ShopRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class MarketApplicationTests {
	@Autowired
	private ShopRepository shopRepository;
	@Test
	void contextLoads() {
		Shop shop = Shop.builder()
				.name("test")
				.status(Status.OPEN)
				.recentTransaction(LocalDateTime.now()) // Directly set the current LocalDateTime
				.build();

		shopRepository.save(shop);
	}

}
