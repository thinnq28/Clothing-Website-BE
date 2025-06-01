package com.datn.shop_app.response.supplier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListSupplierResponse {
    private List<SupplierResponse> suppliers;
    private int totalPages;
}
