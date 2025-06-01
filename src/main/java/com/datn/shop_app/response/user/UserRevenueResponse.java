package com.datn.shop_app.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRevenueResponse {
    private Integer userId;
    private String fullName;
    private String email;
    private BigDecimal totalRevenue;
}
