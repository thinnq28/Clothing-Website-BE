package com.datn.shop_app.service;

import com.datn.shop_app.DTO.OrderDTO;
import com.datn.shop_app.DTO.UpdateStatusOrderDTO;
import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.response.order.BestsellerProductResponse;
import com.datn.shop_app.response.order.OrderDetailResponse;
import com.datn.shop_app.response.order.OrderResponse;
import com.datn.shop_app.response.product.ProductResponse;
import com.datn.shop_app.response.user.UserRevenueResponse;
import com.datn.shop_app.response.variant.VariantResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {
    Page<OrderResponse> getOrders(String fullName, String phoneNumber, String email, LocalDate orderDate, String status, Boolean active, Pageable pageable);

    Page<OrderResponse> getOrders(Integer userId, String fullName, String phoneNumber, String email, LocalDate orderDate, String status, Boolean active, Pageable pageable);

    Order updateStatus(Integer id, UpdateStatusOrderDTO orderDTO);

    Order updatePaymentStatus(Integer id, UpdateStatusOrderDTO orderDTO);

    Order getOrderById(Integer id);

    Order getOrderById(Integer id, User user);

    Page<OrderResponse> getOrderByPhoneNumber(String phoneNumber, Pageable pageable);

    List<OrderDetailResponse> getOrderDetails(Integer orderId);

    Order createOrder(OrderDTO orderDTO);

    List<String> validOrder(OrderDTO orderDTO, BindingResult bindingResult);

    List<Order> getOrdersByTimeFilter(String filter);

    Page<BestsellerProductResponse> findBestsellerProducts(Pageable pageable);

    Page<UserRevenueResponse> getTopUsersRevenue();
}
