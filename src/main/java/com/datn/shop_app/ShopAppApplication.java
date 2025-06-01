package com.datn.shop_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShopAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopAppApplication.class, args);
	}

}
