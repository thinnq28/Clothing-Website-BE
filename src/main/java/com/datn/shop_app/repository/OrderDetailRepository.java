package com.datn.shop_app.repository;

import com.datn.shop_app.entity.OrderDetail;
import com.datn.shop_app.response.order.BestsellerProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderId(int orderId);

    @Query("""
    SELECT new com.datn.shop_app.response.order.BestsellerProductResponse(
        od.variant.product.id,
        od.variant.product.productName,
        od.variant.product.imageUrl,
        SUM(od.numberOfProduct)
    )
    FROM OrderDetail od
    GROUP BY od.variant.product.id, od.variant.product.productName, od.variant.product.imageUrl
    ORDER BY SUM(od.numberOfProduct) DESC
    """)
    Page<BestsellerProductResponse> findBestsellerProducts(Pageable pageable);


}