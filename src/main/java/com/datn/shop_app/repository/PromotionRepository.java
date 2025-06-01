package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
  }