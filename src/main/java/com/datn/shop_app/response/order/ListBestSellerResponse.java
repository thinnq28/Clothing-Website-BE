package com.datn.shop_app.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListBestSellerResponse {
    private List<BestsellerProductResponse> bestsellerProducts;
    private Integer totalPages;
}
