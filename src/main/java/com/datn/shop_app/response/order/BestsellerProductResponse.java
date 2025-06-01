package com.datn.shop_app.response.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BestsellerProductResponse {
    private Integer productId;
    private String productName;
    private String imageUrl;
    private Long totalSold;
    // getter + setter
}
