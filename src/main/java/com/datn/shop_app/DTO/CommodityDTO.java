package com.datn.shop_app.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommodityDTO {
    @NotBlank(message = "{commodity.name.not_blank}")
    @NotNull(message = "{commodity.name.not_null}")
    private String commodityName;
}
