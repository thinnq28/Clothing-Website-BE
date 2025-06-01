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
public class InsertVariantDTO {

    private Integer quantity;

    @NotNull(message = "{insert_variant.price.not_null}")
    @Min(value = 0, message = "{insert_variant.price.min}")
    private Float price;

    @NotNull(message = "{insert_variant.product.not_null}")
    private Integer productId;

    List<OptionVariantDTO> options;
}
