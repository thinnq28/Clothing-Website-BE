package com.datn.shop_app.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionVariantDTO {
    @NotNull(message = "{promotion_variant.variant.not_null}")
    private int variantId;
    @NotNull(message = "{promotion_variant.promotion.not_null}")
    private int promotionId;
}
