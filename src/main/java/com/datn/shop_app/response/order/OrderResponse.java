package com.datn.shop_app.response.order;

import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.entity.Voucher;
import com.datn.shop_app.entity.VoucherOrder;
import com.datn.shop_app.response.variant.VariantResponse;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderResponse {
    private Integer id;

    private String fullName;

    private String phoneNumber;

    private String email;

    private String address;

    private LocalDate orderDate;

    private String paymentMethod;

    private String paymentStatus;

    private String status;

    private Boolean active;

    @JsonProperty("total_money")
    private Double totalMoney;

    private List<OrderDetailResponse> items;

    private Double totalVoucherPercentage;

    private Double totalVoucherFixed;

    public static OrderResponse fromOrder(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        BeanUtils.copyProperties(order, orderResponse);
        orderResponse.setTotalMoney(order.getTotal().doubleValue());
        if(order.getOrderDetails() != null) {
            orderResponse.setItems(order.getOrderDetails().stream().map(e -> {
                OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                BeanUtils.copyProperties(e, orderDetailResponse);
                orderDetailResponse.setNumberOfProduct(e.getNumberOfProduct());
                VariantResponse variantResponse = new VariantResponse();
                BeanUtils.copyProperties(e.getVariant(), variantResponse);
                orderDetailResponse.setVariant(variantResponse);
                return orderDetailResponse;
            }).collect(Collectors.toList()));
        }

        Double totalVoucherPercentage = 0.0;
        Double totalVoucherFixed = 0.0;
        if(order.getVoucherOrders() != null) {
            for (VoucherOrder v : order.getVoucherOrders()) {
                Voucher voucher = v.getVoucher();
                if(voucher != null) {
                    if(Objects.equals(voucher.getDiscountType(), "percentage")) {
                        totalVoucherPercentage += voucher.getDiscount().doubleValue();
                    }

                    if(Objects.equals(voucher.getDiscountType(), "fixed")) {
                        totalVoucherFixed += voucher.getDiscount().doubleValue();
                    }
                }
            }
        }


        orderResponse.setTotalVoucherPercentage(totalVoucherPercentage);
        orderResponse.setTotalVoucherFixed(totalVoucherFixed);

        return orderResponse;
    }
}
