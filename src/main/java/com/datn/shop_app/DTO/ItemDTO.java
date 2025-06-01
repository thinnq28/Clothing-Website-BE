package com.datn.shop_app.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemDTO {
    private String itemName;
    private Integer itemPrice;
    private Integer itemQuantity;

}
