package com.datn.shop_app.DTO;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherDTO {
    @Size(max = 50, message = "{voucher.code.size}")
    @NotNull(message = "{voucher.code.not_null}")
    private String code;

    @Size(max = 255, message = "{voucher.description.size}")
    private String description;

    @NotNull(message = "{voucher.discount.not_null}")
    @Min(value = 0, message = "{voucher.discount.min}")
    private BigDecimal discount;

    @NotNull(message = "{voucher.discount_type.not_null}")
    @NotBlank(message = "{voucher.discount_type.not_blank}")
    private String discountType;

    @NotNull(message = "{voucher.start_date.not_null}")
    @FutureOrPresent(message = "{voucher.start_date.future_or_present}")
    private LocalDate startDate;

    @NotNull(message = "{voucher.end_date.not_null}")
    @FutureOrPresent(message = "{voucher.end_date.future_or_present}")
    private LocalDate endDate;

    @Digits(integer = 10, fraction = 2, message = "{voucher.min_purchase_amount.digits}")
    @PositiveOrZero(message = "{voucher.min_purchase_amount.positive_or_zero}")
    private BigDecimal minPurchaseAmount;

    @Digits(integer = 10, fraction = 2, message = "{voucher.max_discount_amount.digits}")
    @PositiveOrZero(message = "{voucher.max_discount_amount.positive_or_zero}")
    private BigDecimal maxDiscountAmount;

    @PositiveOrZero(message = "{voucher.max_usage.positive_or_zero}")
    private Integer maxUsage;
}
