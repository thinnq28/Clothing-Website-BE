package com.datn.shop_app.DTO;

import com.datn.shop_app.model.PurchaseOrderModel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDTO {
    @NotNull(message = "{purchase_order.supplier.not_null}")
    private Integer supplierId;

    @NotNull(message = "{purchase_order.total_amount.not_null}")
    @Min(value = 0, message = "{purchase_order.total_amount.min}")
    private BigDecimal totalAmount;

    private List<PurchaseOrderModel> purchaseOrderModels;
}
