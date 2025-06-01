package com.datn.shop_app.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CheckoutDTO {
    private int amount;
    private List<ItemDTO> items;
    private int orderId;
    private String returnUrl;
    private String cancelUrl;

}
