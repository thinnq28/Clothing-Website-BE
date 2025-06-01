package com.datn.shop_app.response.order;


import com.datn.shop_app.response.variant.VariantResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NotEmpty
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private Integer id;

    @JsonProperty("quantity")
    private Integer numberOfProduct;

    private Double price;

    private VariantResponse variant;
}
