package com.datn.shop_app.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuantityVariantDTO {
    @NotNull(message = "{quantity_variant.quantity.not_null}")
    @Min(value = 0, message = "{quantity_variant.quantity.min}")
    private Integer quantity;

    @NotNull(message = "{quantity_variant.sku_id.not_null}")
    @NotBlank(message = "{quantity_variant.sku_id.not_blank}")
    private String SkuId;
}
