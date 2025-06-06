package com.datn.shop_app.response.voucher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class ListVoucherResponse {
    private List<VoucherResponse> vouchers;
    private int totalPages;
}
