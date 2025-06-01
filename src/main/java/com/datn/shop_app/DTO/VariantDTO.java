package com.datn.shop_app.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VariantDTO {

    private Integer quantity;

    @NotNull(message = "{variant.price.not_null}")
    @Min(value = 0, message = "{variant.price.min}")
    private Float price;

    @NotNull(message = "{variant.product.not_null}")
    private Integer productId;

    @NotEmpty(message = "{variant.property.not_empty}")
    private List<Integer> properties;

    private List<Integer> optionIds;
}
