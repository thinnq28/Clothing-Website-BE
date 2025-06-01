package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.CartItemDTO;
import com.datn.shop_app.DTO.OrderDTO;
import com.datn.shop_app.DTO.UpdateStatusOrderDTO;
import com.datn.shop_app.entity.*;
import com.datn.shop_app.repository.*;
import com.datn.shop_app.response.order.BestsellerProductResponse;
import com.datn.shop_app.response.order.OrderDetailResponse;
import com.datn.shop_app.response.order.OrderResponse;
import com.datn.shop_app.response.user.UserRevenueResponse;
import com.datn.shop_app.response.variant.VariantResponse;
import com.datn.shop_app.service.OrderService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    final List<String> paymentMethods = List.of("cod", "other");
    final List<String> statuses = List.of("pending", "processing", "shipped", "delivered");
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final VariantRepository variantRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final VoucherOrderRepository voucherOrderRepository;
    private final ProductRepository productRepository;
    private final LocalizationUtils localizationUtils;

    @Override
    public Page<OrderResponse> getOrders(String fullName, String phoneNumber, String email, LocalDate orderDate, String status, Boolean active, Pageable pageable) {
        Page<Order> orders = orderRepository.findAllOder(fullName, phoneNumber, email, orderDate, status, active, pageable);
        return orders.map(OrderResponse::fromOrder);
    }

    @Override
    public Page<OrderResponse> getOrders(Integer userId, String fullName, String phoneNumber, String email, LocalDate orderDate, String status, Boolean active, Pageable pageable) {
        Page<Order> orders = orderRepository.findAllOder(userId, fullName, phoneNumber, email, orderDate, status, active, pageable);
        return orders.map(OrderResponse::fromOrder);
    }

    @Override
    public Order updateStatus(Integer id, UpdateStatusOrderDTO orderDTO) {
        Order order = orderRepository.findByIdAndActive(id, true);
        if (order != null) {
            order.setStatus(orderDTO.getStatus());
            orderRepository.save(order);

            if(Objects.equals("cancelled", orderDTO.getStatus()) && order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                List<OrderDetail> orderDetails = order.getOrderDetails();
                List<Variant> variants = new ArrayList<>();
                for (OrderDetail orderDetail : orderDetails) {
                    Variant variant = orderDetail.getVariant();
                    variant.setQuantity(variant.getQuantity() + orderDetail.getNumberOfProduct());
                    variants.add(variant);
                }
                variantRepository.saveAll(variants);
            }

            return order;
        }

        return null;
    }


    @Override
    public Order updatePaymentStatus(Integer id, UpdateStatusOrderDTO orderDTO) {
        Order order = orderRepository.findByIdAndActive(id, true);
        if (order != null) {
            order.setPaymentStatus(orderDTO.getStatus());
            orderRepository.save(order);

            if(Objects.equals("paid", orderDTO.getStatus()) && order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                List<OrderDetail> orderDetails = order.getOrderDetails();
                List<Variant> variants = new ArrayList<>();
                for (OrderDetail orderDetail : orderDetails) {
                    Variant variant = orderDetail.getVariant();
                    variant.setQuantity(variant.getQuantity() - orderDetail.getNumberOfProduct());
                    variants.add(variant);
                }
                variantRepository.saveAll(variants);
            }
            return order;
        }

        return null;
    }

    @Override
    public Order getOrderById(Integer id) {
        return orderRepository.findByIdAndActive(id, true);
    }

    @Override
    public Order getOrderById(Integer id, User user) {
        return orderRepository.findByIdAndActiveAndUser(id, true, user);
    }

    @Override
    public Page<OrderResponse> getOrderByPhoneNumber(String phoneNumber, Pageable pageable) {
        Page<Order> orders = orderRepository.findOrderByPhoneNumber(phoneNumber, pageable);
        return orders.map(OrderResponse::fromOrder);
    }

    @Override
    public List<OrderDetailResponse> getOrderDetails(Integer orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();

        for (OrderDetail orderDetail : orderDetails) {
            OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
            BeanUtils.copyProperties(orderDetail, orderDetailResponse);
            VariantResponse variantResponse = VariantResponse.fromVariant(orderDetail.getVariant());
            orderDetailResponse.setVariant(variantResponse);
            orderDetailResponses.add(orderDetailResponse);
        }

        return orderDetailResponses;
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        order.setOrderDate(LocalDate.now());
        if (orderDTO.getUserId() != null) {
            Optional<User> user = userRepository.findById(orderDTO.getUserId());
            user.ifPresent(order::setUser);
        }

        if(orderDTO.getUserId() != null) {
            Optional<User> user = userRepository.findById(orderDTO.getUserId());
            user.ifPresent(order::setUpdatedBy);
        }

        order.setActive(true);
        order.setTotal(orderDTO.getTotal());
        order = orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItemDTO cartItem : orderDTO.getCartItems()) {
             Optional<Variant> variant = variantRepository.findByIdAndActive(cartItem.getVariantId(), true);
             if(variant.isPresent()) {
                 variant.get().setQuantity(variant.get().getQuantity() - cartItem.getQuantity());
                 variantRepository.save(variant.get());

                 OrderDetail orderDetail = new OrderDetail();
                 orderDetail.setOrder(order);
                 orderDetail.setVariant(variant.get());
                 orderDetail.setPrice(variant.get().getPrice().doubleValue());
                 orderDetail.setNumberOfProduct(cartItem.getQuantity());
                 orderDetails.add(orderDetail);
             }
        }

        orderDetailRepository.saveAll(orderDetails);

        if (orderDTO.getCodes() != null && !orderDTO.getCodes().isEmpty()) {
            List<VoucherOrder> voucherOrders = new ArrayList<>();
            for (String code : orderDTO.getCodes()) {
                Voucher voucher = voucherRepository.findByCodeAndActive(code, true);
                if(voucher != null) {

                    voucher.setTimesUsed(voucher.getTimesUsed() + 1);
                    voucherRepository.save(voucher);

                    VoucherOrder voucherOrder = new VoucherOrder();
                    voucherOrder.setOrder(order);
                    voucherOrder.setVoucher(voucher);
                    voucherOrders.add(voucherOrder);
                }
            }

            voucherOrderRepository.saveAll(voucherOrders);
        }

        return order;
    }

    @Override
    public List<String> validOrder(OrderDTO orderDTO, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
        }

        if(orderDTO.getEmail() != null && !orderDTO.getEmail().isEmpty() && !orderDTO.getEmail().matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.PATTERN_EMAIL));
        }

        List<CartItemDTO> cartItems = orderDTO.getCartItems();
        if (cartItems == null || cartItems.isEmpty()) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.CART_ITEM_IS_NOT_EMPTY));
        } else {
            List<Integer> variantIds = cartItems.stream().map(CartItemDTO::getVariantId).toList();
            for (Integer variantId : variantIds) {
                Optional<Variant> variant = variantRepository.findByIdAndActive(variantId, true);
                Optional<CartItemDTO> cartItemDTO =  cartItems.stream().filter(cartItem -> cartItem.getVariantId().equals(variantId)).findFirst();

                if (variant.isEmpty()) {
                    errors.add(localizationUtils.getLocalizedMessage(MessageKeys.UNKNOWN_VARIANT_IS_NOT_EXISTS));
                } else  {
                    if(variant.get().getQuantity() <= 0 ||
                            cartItemDTO.get().getQuantity() > variant.get().getQuantity())
                        errors.add(localizationUtils.getLocalizedMessage(MessageKeys.UNKNOWN_VARIANT_IS_OUT_OF_STOCK));
                }
            }
        }

        if (orderDTO.getUserId() != null && orderDTO.getUserId() > 0) {
            Optional<User> user = userRepository.findById(orderDTO.getUserId());
            if (user.isEmpty()) {
                errors.add(localizationUtils.getLocalizedMessage(MessageKeys.USER_IS_NOT_FOUND));
            }
        }

        if (orderDTO.getCodes() != null && !orderDTO.getCodes().isEmpty()) {
            List<Voucher> vouchers = voucherRepository.getVouchersByCode(orderDTO.getCodes());
            for (Voucher voucher : vouchers) {
                if (voucher == null) {
                    errors.add(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_IS_NOT_EXISTS));
                } else if (LocalDate.now().isBefore(voucher.getStartDate())) {
                    errors.add(
                            String.format(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_NAME_HAS_NOT_APPLIED), voucher.getCode())
                    );
                } else if (LocalDate.now().isAfter(voucher.getEndDate())) {
                    errors.add(String.format(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_NAME_IS_EXPIRED), voucher.getCode()));
                } else if (voucher.getMaxUsage() != null && voucher.getTimesUsed() > voucher.getMaxUsage()) {
                    errors.add(String.format(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_NAME_IS_MAX_USAGE), voucher.getCode()));
                }
            }
        }

        if (!paymentMethods.contains(orderDTO.getPaymentMethod())) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.PAYMENT_METHOD_IS_NOT_SUPPORTED));
        }

        if (!statuses.contains(orderDTO.getStatus())) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.STATUS_ORDER_IS_NOT_SUPPORTED));
        }

        return errors;
    }

    @Override
    public List<Order> getOrdersByTimeFilter(String filter) {
        LocalDate startDate = null;
        LocalDate endDate = null;

        LocalDate now = LocalDate.now();

        switch (filter) {
            case "this_week":
                startDate = now.with(DayOfWeek.MONDAY);
                endDate = now.with(DayOfWeek.SUNDAY);
                break;
            case "this_month":
                startDate = now.withDayOfMonth(1);
                endDate = now.withDayOfMonth(now.lengthOfMonth());
                break;
            case "this_year":
                startDate = now.withDayOfYear(1);
                endDate = now.withDayOfYear(now.lengthOfYear());
                break;
            default:
                // Nếu không có filter hợp lệ thì để null => không lọc theo ngày
                startDate = now.with(DayOfWeek.MONDAY);
                endDate = now.with(DayOfWeek.SUNDAY);
                break;
        }

        return orderRepository.findOrdersByDateAndActive(startDate, endDate);
    }

    @Override
    public Page<BestsellerProductResponse> findBestsellerProducts(Pageable pageable) {
        Page<BestsellerProductResponse> bestsellerProductResponses = orderDetailRepository.findBestsellerProducts(pageable);
        return bestsellerProductResponses;
    }

    @Override
    public Page<UserRevenueResponse> getTopUsersRevenue() {
        Page<UserRevenueResponse> top5Users = orderRepository.getTopUsersRevenue(PageRequest.of(0, 5));
        return top5Users;
    }

}
