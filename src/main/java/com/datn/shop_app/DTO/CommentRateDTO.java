package com.datn.shop_app.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CommentRateDTO {

    @NotNull(message = "Bạn chưa chọn sản phẩm cần đánh giá")
    @JsonProperty("product_id")
    private Integer productId;

    @NotNull(message = "Bạn chưa đánh giá sao cho sản phẩm")
    @Min(value = 1, message = "Số sao bạn đánh giá phải tối thiểu là 1 và tối đa là 5")
    @Max(value = 5, message = "Số sao bạn đánh giá phải tối thiểu là 1 và tối đa là 5")
    private Integer rating;

    private String content;
}
