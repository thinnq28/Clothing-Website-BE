package com.datn.shop_app.repository;

import com.datn.shop_app.entity.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Integer> {
}