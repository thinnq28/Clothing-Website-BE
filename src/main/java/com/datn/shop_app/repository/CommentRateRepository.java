package com.datn.shop_app.repository;

import com.datn.shop_app.entity.CommentRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRateRepository extends JpaRepository<CommentRate, Integer> {
    Page<CommentRate> findAllByProductId(Integer productId, Pageable pageable);
}