package com.datn.shop_app.DTO;

import com.datn.shop_app.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Integer userId;

    @Size(max = 30, message = "{order.full_name.size}")
    @NotNull(message = "{order.full_name.not_null}")
    @NotBlank(message = "{order.full_name.not_blank}")
    private String fullName;

    @Size(max = 10, message = "{order.phone_number.size}")
    @NotNull(message = "{order.phone_number.not_null}")
    @NotBlank(message = "{order.phone_number.not_blank}")
    private String phoneNumber;

    @Size(max = 100, message = "{order.email.size}")
    private String email;

    @Size(max = 255, message = "{order.address.size}")
    private String address;

    @Size(max = 255, message = "{order.note.size}")
    private String note;

    @NotNull(message = "{order.paymentMethod.not_null}")
    @NotBlank(message = "{order.paymentMethod.not_blank}")
    private String paymentMethod;

    @NotNull(message = "{order.status.not_null}")
    @NotBlank(message = "{order.status.not_blank}")
    private String status;

    private List<String> codes;

    @JsonProperty("totalMoney")
    private BigDecimal total;

    @JsonProperty("cart_items")
    private List<CartItemDTO> cartItems;

    private Integer updatedBy;
}
