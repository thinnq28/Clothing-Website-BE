package com.datn.shop_app.controller.user;


import com.datn.shop_app.DTO.CheckoutDTO;
import com.datn.shop_app.DTO.ItemDTO;
import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.OrderDetail;
import com.datn.shop_app.repository.OrderRepository;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/client")
@RequiredArgsConstructor
public class CheckoutController {
    private final PayOS payOS;

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @Value("${domain.front-end}")
    private String domainFrontEnd;

    @PostMapping("create-payment-link")
    public ResponseEntity<ResponseObject> checkout(
            @RequestBody CheckoutDTO checkoutDTO) {
        try {
            Long orderCode = System.currentTimeMillis() / 1000;
            List<ItemData> itemDataList = new ArrayList<>();
            List<ItemDTO> items = checkoutDTO.getItems();
            if (!items.isEmpty()) {
                for (ItemDTO itemDTO : items) {
                    ItemData itemData = ItemData.builder()
                            .name(itemDTO.getItemName())
                            .price(itemDTO.getItemPrice())
                            .quantity(itemDTO.getItemQuantity())
                            .build();
                    itemDataList.add(itemData);
                }
            }

            PaymentData paymentData = PaymentData
                    .builder()
                    .orderCode(orderCode)
                    .amount(checkoutDTO.getAmount())
                    .description("Checkout")
                    .returnUrl(domainFrontEnd + checkoutDTO.getReturnUrl())
                    .cancelUrl(domainFrontEnd + checkoutDTO.getCancelUrl())
                    .items(itemDataList)
                    .build();

            CheckoutResponseData result = payOS.createPaymentLink(paymentData);

            Order order = orderService.getOrderById(checkoutDTO.getOrderId());
            if (order != null) {
                order.setOrderCode(String.valueOf(orderCode));
                orderRepository.save(order);
            }

            return ResponseEntity.ok()
                    .body(ResponseObject.builder()
                            .status(HttpStatus.OK)
                            .message("Tao QR Thanh Cong")
                            .data(result).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message("Tao QR That Bai")
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
}
