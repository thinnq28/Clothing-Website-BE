package com.datn.shop_app.DTO;

import com.datn.shop_app.entity.Supplier;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    @NotNull(message = "{product.name.not_null}")
    @NotBlank(message = "{product.name.not_blank}")
    @JsonProperty("name")
    private String productName;

    @NotNull(message = "{product.supplier.not_null}")
    private Integer supplierId;

    @NotNull(message = "{product.commodity.not_null}")
    private Integer CommodityId;

    private String description;

    @JsonProperty("optionId")
    private List<Integer> optionIds;
}
