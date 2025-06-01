package com.datn.shop_app.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateVariantDTO {
    @NotNull(message = "{update_variant.quantity.not_null}")
    @Min(value = 0, message = "{update_variant.quantity.min}")
    private Integer quantity;

    @NotNull(message = "{update_variant.price.not_null}")
    @Min(value = 0, message = "{update_variant.price.min}")
    private Float price;

    @NotNull(message = "{update_variant.product.not_null}")
    private Integer productId;

    @NotEmpty(message = "{update_variant.property.not_empty}")
    private List<Integer> properties;

    private List<Integer> imageIds;
}
