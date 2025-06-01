package com.datn.shop_app.controller.admin;

import com.datn.shop_app.DTO.OrderDTO;
import com.datn.shop_app.DTO.UpdateStatusOrderDTO;
import com.datn.shop_app.constant.Constant;
import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.OrderDetail;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.entity.Variant;
import com.datn.shop_app.repository.OrderRepository;
import com.datn.shop_app.repository.VariantRepository;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.order.ListOrderResponse;
import com.datn.shop_app.response.order.OrderDetailResponse;
import com.datn.shop_app.response.order.OrderResponse;
import com.datn.shop_app.response.product.ProductResponse;
import com.datn.shop_app.response.user.UserResponse;
import com.datn.shop_app.response.variant.VariantResponse;
import com.datn.shop_app.response.voucher.ListVoucherResponse;
import com.datn.shop_app.response.voucher.VoucherResponse;
import com.datn.shop_app.service.OrderService;
import com.datn.shop_app.service.ProductService;
import com.datn.shop_app.service.UserService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    private final UserService userService;

    private final LocalizationUtils localizationUtils;
    private final OrderRepository orderRepository;
    private final VariantRepository variantRepository;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ORDER') or hasRole('ROLE_USER')")
    public ResponseEntity<ResponseObject> getAllVouchers(@RequestParam(defaultValue = "") String fullName,
                                                         @RequestParam(defaultValue = "") String phoneNumber,
                                                         @RequestParam(defaultValue = "") String email,
                                                         @RequestParam(defaultValue = "") LocalDate orderDate,
                                                         @RequestParam(defaultValue = "") String status,
                                                         @RequestParam(defaultValue = "true", name = "active") Boolean active,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<OrderResponse> orderPage = orderService.getOrders(fullName, phoneNumber, email, orderDate, status, active, pageRequest);
        totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();

        ListOrderResponse listOrderResponse = ListOrderResponse.builder()
                .orders(orderResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_ORDER_DETAIL_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(listOrderResponse)
                .build());

    }

    @PutMapping("/update-status/{code}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ORDER')")
    public ResponseEntity<ResponseObject> updateStatus(@PathVariable("code") Integer id,
                                                       @Valid @RequestBody UpdateStatusOrderDTO orderDTO,
                                                       BindingResult bindingResult) {
        List<String> statuses = List.of("pending", "processing", "shipped", "delivered", "cancelled");
        List<String> errors = new ArrayList<>();

        Order order = orderService.getOrderById(id);
        if (order == null) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND));
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_STATUS_FOR_ORDER_FAILED)).build());
        }

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
        }

        if (!statuses.contains(orderDTO.getStatus())) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.STATUS_MUST_FOLLOW) + " "
                    + String.join(",", statuses));
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_STATUS_FOR_ORDER_FAILED)).build());
        }

        order = orderService.updateStatus(id, orderDTO);
        if (order == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_STATUS_FOR_ORDER_FAILED)).build());
        }

        OrderResponse orderResponse = OrderResponse.fromOrder(order);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(orderResponse)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_STATUS_FOR_ORDER_SUCCESSFULLY)).build());
    }


    @PutMapping("/update/payment-status/{code}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ORDER')")
    public ResponseEntity<ResponseObject> updatePaymentStatus(@PathVariable("code") Integer id,
                                                       @Valid @RequestBody UpdateStatusOrderDTO orderDTO,
                                                       BindingResult bindingResult) {
        List<String> statuses = List.of("unpaid", "paid");
        List<String> errors = new ArrayList<>();

        Order order = orderService.getOrderById(id);
        if (order == null) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND));
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_STATUS_FOR_ORDER_FAILED)).build());
        }

        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                errors.add(fieldError.getDefaultMessage());
            }
        }

        if (!statuses.contains(orderDTO.getStatus())) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.STATUS_MUST_FOLLOW) + " "
                    + String.join(",", statuses));
        }
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_STATUS_FOR_ORDER_FAILED)).build());
        }

        if(Objects.equals("paid", orderDTO.getStatus()) && order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            List<OrderDetail> orderDetails = order.getOrderDetails();
            for (OrderDetail orderDetail : orderDetails) {
                Variant variant = orderDetail.getVariant();
                if((variant.getQuantity() - orderDetail.getNumberOfProduct()) < 0) {
                    return ResponseEntity.badRequest().body(ResponseObject.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .data(errors)
                            .message(variant.getVariantName() + " Đã hết hàng, hãy cập nhật số lượng lại cho variant").build());
                }
            }
        }

        order = orderService.updatePaymentStatus(id, orderDTO);
        if (order == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_STATUS_FOR_ORDER_FAILED)).build());
        }

        OrderResponse orderResponse = OrderResponse.fromOrder(order);

        return ResponseEntity.ok().body(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(orderResponse)
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_STATUS_FOR_ORDER_SUCCESSFULLY)).build());
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<ResponseObject> cancel(@PathVariable Integer id,
                                                 @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        String extractedToken = authorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
        User user = userService.getUserDetailsFromToken(extractedToken);
        Integer userId = user == null ? null : user.getId();

        Order order = orderService.getOrderById(id, user);
        if (order == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND))
                    .status(HttpStatus.BAD_REQUEST)
                    .data("Đơn hàng này của bạn không tồn tại").build());
        }

        if (!Objects.equals(order.getStatus(), Constant.OrderStatus.PENDING)) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_MODIFY))
                    .status(HttpStatus.BAD_REQUEST)
                    .data("Bạn không thể huỷ đơn hàng khi đã được xác nhận").build());
        }

            order.setStatus(Constant.OrderStatus.CANCELLED);
            orderRepository.save(order);

            List<OrderDetailResponse> orderDetailResponses = orderService.getOrderDetails(id);

            // Lấy danh sách variantId
            List<Integer> variantIds = orderDetailResponses.stream()
                    .map(e -> e.getVariant().getId())
                    .toList();

            // Lấy danh sách Variant từ DB và tạo Map để tra cứu nhanh
            List<Variant> variants = variantRepository.getVariantsByIds(variantIds);
            Map<Integer, Variant> variantMap = variants.stream()
                    .collect(Collectors.toMap(Variant::getId, Function.identity()));

            // Cập nhật số lượng và lưu
            for (OrderDetailResponse orderDetail : orderDetailResponses) {
                int variantId = orderDetail.getVariant().getId();
                Variant variant = variantMap.get(variantId);
                if (variant != null) {
                    variant.setQuantity(variant.getQuantity() + orderDetail.getNumberOfProduct());
                }
            }

            // Lưu toàn bộ danh sách variant một lần (tốt hơn so với lưu từng cái một)
            variantRepository.saveAll(variantMap.values());

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.CANCELING_ORDER_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data("Huỷ đơn hàng thành công")
                    .build());
        }

        @PostMapping
        public ResponseEntity<ResponseObject> createOrder (@Valid @RequestBody OrderDTO orderDTO, BindingResult
        bindingResult){
            List<String> errors = orderService.validOrder(orderDTO, bindingResult);
            if (!errors.isEmpty()) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message("Create order is not successful")
                        .status(HttpStatus.BAD_REQUEST)
                        .data(errors)
                        .build());
            }

            Order order = orderService.createOrder(orderDTO);

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message("Create order is successful")
                    .status(HttpStatus.OK)
                    .data(OrderResponse.fromOrder(order))
                    .build());
        }

        @PostMapping("/orders-of-client")
        public ResponseEntity<ResponseObject> getOrdersOfClient (@RequestHeader("Authorization") String
        authorizationHeader,
                @RequestParam(defaultValue = "") String fullName,
                @RequestParam(defaultValue = "") String phoneNumber,
                @RequestParam(defaultValue = "") String email,
                @RequestParam(defaultValue = "") LocalDate orderDate,
                @RequestParam(defaultValue = "") String status,
                @RequestParam(defaultValue = "true", name = "active") Boolean active,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) throws Exception {

            String extractedToken = authorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
            User user = userService.getUserDetailsFromToken(extractedToken);
            Integer userId = user == null ? null : user.getId();

            int totalPages = 0;
            PageRequest pageRequest = PageRequest.of(page, limit);
            Page<OrderResponse> orderPage = orderService.getOrders(userId, fullName, phoneNumber, email, orderDate, status, active, pageRequest);
            totalPages = orderPage.getTotalPages();
            List<OrderResponse> orderResponses = orderPage.getContent();

            ListOrderResponse listOrderResponse = ListOrderResponse.builder()
                    .orders(orderResponses)
                    .totalPages(totalPages).build();

            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_ORDER_DETAIL_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data(listOrderResponse)
                    .build());
        }

        @PostMapping("/order-detail/{order_id}")
        public ResponseEntity<ResponseObject> getOrderDetail (@RequestHeader("Authorization") String
        authorizationHeader,
                @PathVariable("order_id") Integer orderId) throws Exception {
            String extractedToken = authorizationHeader.substring(7); // Loại bỏ "Bearer " từ chuỗi token
            User user = userService.getUserDetailsFromToken(extractedToken);

            Order order = orderService.getOrderById(orderId, user);
            if (order == null) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND))
                        .status(HttpStatus.BAD_REQUEST)
                        .data("Đơn hàng này của bạn không tồn tại").build());
            }

            List<OrderDetailResponse> orderDetailResponses = orderService.getOrderDetails(orderId);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_ORDER_DETAIL_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data(orderDetailResponses)
                    .build());
        }

        @GetMapping("/{id}")
        public ResponseEntity<ResponseObject> getOrderById (@PathVariable("id") Integer orderId) throws Exception {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.badRequest().body(ResponseObject.builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND))
                        .status(HttpStatus.BAD_REQUEST)
                        .data("").build());
            }

            OrderResponse orderResponse = OrderResponse.fromOrder(order);
            return ResponseEntity.ok().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_ORDER_DETAIL_SUCCESSFULLY))
                    .status(HttpStatus.OK)
                    .data(orderResponse)
                    .build());
        }
    }
