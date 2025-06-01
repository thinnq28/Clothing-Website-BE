package com.datn.shop_app.DTO;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {
    @NotNull(message = "{promotion.name.not_null}")
    @NotBlank(message = "{promotion.name.not_blank}")
    private String name;

    @NotNull(message = "{promotion.discount_percentage.not_blank}")
    @PositiveOrZero(message = "{promotion.discount_percentage.positive_or_zero}")
    @Max(value = 100, message = "{promotion.discount_percentage.max}")
    private BigDecimal discountPercent;

    @NotNull(message = "{promotion.discount_percentage.not_blank}")
    @PositiveOrZero(message = "{promotion.discount_percentage.positive_or_zero}")
    @Max(value = 100, message = "{promotion.discount_percentage.max}")
    private BigDecimal discountAmount   ;

    @NotNull(message = "{promotion.start_date.not_null}")
    @FutureOrPresent(message = "{promotion.start_date.future_or_present}")
    private LocalDate startDate;

    @NotNull(message = "{promotion.end_date.not_null}")
    @FutureOrPresent(message = "{promotion.end_date.future_or_present}")
    private LocalDate endDate;

    private List<Integer> productIds; // Danh sách ID sản phẩm được áp dụng giảm giá

    private List<Integer> userIds;
}
