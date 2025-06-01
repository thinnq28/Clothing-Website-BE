package com.datn.shop_app.controller.user;

import com.datn.shop_app.constant.Constant;
import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.OrderDetail;
import com.datn.shop_app.entity.Variant;
import com.datn.shop_app.repository.OrderRepository;
import com.datn.shop_app.repository.VariantRepository;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.order.*;
import com.datn.shop_app.response.user.UserRevenueResponse;
import com.datn.shop_app.service.OrderService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/client/order")
@RequiredArgsConstructor
public class ClientOrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final LocalizationUtils localizationUtils;
    private final VariantRepository variantRepository;

    @PostMapping("/paid/{orderCode}")
    public ResponseEntity<ResponseObject> updateOrder(@PathVariable String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode);
        if (order != null) {
            order.setPaymentStatus(Constant.Order.PAID);
            orderRepository.save(order);

            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .message("Cập nhật thành công")
                            .status(HttpStatus.OK).build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND))
                            .status(HttpStatus.BAD_REQUEST).build()
            );
        }
    }

    @PostMapping("/unpaid/{orderCode}")
    public ResponseEntity<ResponseObject> updateUnPaid(@PathVariable String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode);
        if (order != null) {
            order.setPaymentStatus(Constant.Order.UNPAID);

            if(order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                List<OrderDetail> orderDetails = order.getOrderDetails();
                List<Variant> variants = new ArrayList<>();
                for (OrderDetail orderDetail : orderDetails) {
                    Variant variant = orderDetail.getVariant();
                    variant.setQuantity(variant.getQuantity() + orderDetail.getNumberOfProduct());
                    variants.add(variant);
                }
                variantRepository.saveAll(variants);
            }

            orderRepository.save(order);

            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .message("Cập nhật thành công")
                            .status(HttpStatus.OK).build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND))
                            .status(HttpStatus.BAD_REQUEST).build()
            );
        }
    }

    @PostMapping("/unpaid/cod/{id}")
    public ResponseEntity<ResponseObject> updateUnPaidCOD(@PathVariable("id") Integer orderCode) {
        Optional<Order> order = orderRepository.findById(orderCode);
        if (order.isPresent()) {
            order.get().setPaymentStatus(Constant.Order.UNPAID);
            orderRepository.save(order.get());

            if(order.get().getOrderDetails() != null && !order.get().getOrderDetails().isEmpty()) {
                List<OrderDetail> orderDetails = order.get().getOrderDetails();
                List<Variant> variants = new ArrayList<>();
                for (OrderDetail orderDetail : orderDetails) {
                    Variant variant = orderDetail.getVariant();
                    variant.setQuantity(variant.getQuantity() + orderDetail.getNumberOfProduct());
                    variants.add(variant);
                }
                variantRepository.saveAll(variants);
            }

            return ResponseEntity.ok().body(
                    ResponseObject.builder()
                            .message("Cập nhật thành công")
                            .status(HttpStatus.OK).build()
            );
        } else {
            return ResponseEntity.badRequest().body(
                    ResponseObject.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND))
                            .status(HttpStatus.BAD_REQUEST).build()
            );
        }
    }

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<ResponseObject> getOrderDetail(@PathVariable("phoneNumber") String phoneNumber,
                                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<OrderResponse> orderPage = orderService.getOrderByPhoneNumber(phoneNumber, pageRequest);
        totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();

        if (orderResponses.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND))
                    .status(HttpStatus.BAD_REQUEST)
                    .data("Đơn hàng này của bạn không tồn tại").build());
        }

        ListOrderResponse listOrderResponse = ListOrderResponse.builder()
                .orders(orderResponses)
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_ORDER_DETAIL_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(listOrderResponse)
                .build());
    }


    @GetMapping("/filter")
    public ResponseEntity<ResponseObject> getOrderChart(
            @RequestParam(defaultValue = "this_week") String filter
    ) {
        List<Order> orders = orderService.getOrdersByTimeFilter(filter);

        // Convert sang OrderResponse
        List<OrderResponse> orderResponses = orders.stream()
                .map(OrderResponse::fromOrder)
                .toList();

        List<String> labels = new ArrayList<>();
        Map<String, Double> dataMap = new HashMap<>();
        LocalDate today = LocalDate.now();

        switch (filter) {
            case "this_week":
                labels = List.of("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");

                dataMap = orderResponses.stream()
                        .filter(o -> o.getOrderDate() != null)
                        .collect(Collectors.groupingBy(
                                o -> o.getOrderDate().getDayOfWeek().name().substring(0, 3).toLowerCase(),
                                Collectors.summingDouble(OrderResponse::getTotalMoney)
                        ));
                break;

            case "this_month":
                int daysInMonth = YearMonth.from(today).lengthOfMonth();
                for (int i = 1; i <= daysInMonth; i++) {
                    labels.add(String.valueOf(i));
                }

                dataMap = orderResponses.stream()
                        .filter(o -> o.getOrderDate() != null)
                        .collect(Collectors.groupingBy(
                                o -> String.valueOf(o.getOrderDate().getDayOfMonth()).toLowerCase(),
                                Collectors.summingDouble(OrderResponse::getTotalMoney)
                        ));
                break;

            case "this_year":
                for (int i = 1; i <= 12; i++) {
                    String monthName = YearMonth.of(today.getYear(), i)
                            .getMonth()
                            .getDisplayName(TextStyle.SHORT, Locale.ENGLISH); // "Jan", "Feb", ...
                    labels.add(monthName);
                }

                dataMap = orderResponses.stream()
                        .filter(o -> o.getOrderDate() != null)
                        .collect(Collectors.groupingBy(
                                o -> o.getOrderDate().getMonth()
                                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase(),
                                Collectors.summingDouble(OrderResponse::getTotalMoney)
                        ));
                break;

            default:
                labels = List.of("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
                break;
        }

        Map<String, Double> finalDataMap = dataMap;
        List<Double> data = labels.stream()
                .map(label -> finalDataMap.getOrDefault(label.toLowerCase(), 0.0))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(ResponseObject.builder()
                        .message("Success")
                        .status(HttpStatus.OK)
                        .data(new ChartDataResponse(labels, data))
                .build());
    }


    @GetMapping("/best-seller-product")
    public ResponseEntity<ResponseObject> findBestsellerProducts(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                @RequestParam(value = "limit", required = false, defaultValue = "9") int limit) {
        int totalPages = 0;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<BestsellerProductResponse> orderPage = orderService.findBestsellerProducts(pageRequest);
        totalPages = orderPage.getTotalPages();

        if (orderPage.getContent().isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND))
                    .status(HttpStatus.BAD_REQUEST)
                    .data("Best Seller Product is empty").build());
        }

        ListBestSellerResponse listOrderResponse = ListBestSellerResponse.builder()
                .bestsellerProducts(orderPage.getContent())
                .totalPages(totalPages).build();

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_ORDER_DETAIL_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(listOrderResponse)
                .build());
    }

    @GetMapping("/best-seller-user")
    public ResponseEntity<ResponseObject> findBestsellerProducts() {

        Page<UserRevenueResponse> orderPage = orderService.getTopUsersRevenue();

        if (orderPage.getContent().isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.ORDER_IS_NOT_FOUND))
                    .status(HttpStatus.BAD_REQUEST)
                    .data("Best Seller is empty").build());
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_ORDER_DETAIL_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(orderPage.getContent())
                .build());
    }

}
