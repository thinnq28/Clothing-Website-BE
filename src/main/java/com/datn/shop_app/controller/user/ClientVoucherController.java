package com.datn.shop_app.controller.user;

import com.datn.shop_app.entity.Order;
import com.datn.shop_app.entity.User;
import com.datn.shop_app.entity.Voucher;
import com.datn.shop_app.repository.OrderRepository;
import com.datn.shop_app.repository.UserRepository;
import com.datn.shop_app.response.ResponseObject;
import com.datn.shop_app.response.voucher.VoucherResponse;
import com.datn.shop_app.service.VariantService;
import com.datn.shop_app.service.VoucherService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/client/voucher")
public class ClientVoucherController {
    private final VoucherService voucherService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final LocalizationUtils localizationUtils;

    @GetMapping("/by-code")
    public ResponseEntity<ResponseObject> getVoucherByVoucherCode(@RequestParam String code,
                                                                  @RequestParam Integer userId) {
        List<String> errors = new ArrayList<>();
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_CODE_IS_REQUIRED))
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }

        Voucher voucher = voucherService.getVoucherByCode(code);

        if (voucher == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_IS_NOT_EXISTS))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .build());
        }

        if (LocalDate.now().isBefore(voucher.getStartDate())) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_HAS_NOT_APPLIED));
        }

        if (LocalDate.now().isAfter(voucher.getEndDate())) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_IS_EXPIRED));
        }


        if (voucher.getMaxUsage() != null && voucher.getTimesUsed() >= voucher.getMaxUsage()) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_IS_MAX_USAGE));
        }

        if (userId != null && userId > 0) {
            Optional<User> user = userRepository.findByIdAndActive(userId, true);
            if (user.isPresent()) {
                List<Order> orders = orderRepository.findByUserId(userId);
                double total = 0.0;
                for (Order order : orders) {
                    total += order.getOrderDetails().stream().mapToDouble(e -> e.getPrice() * e.getNumberOfProduct()).sum();
                }
                BigDecimal doubleAsBigDecimal = BigDecimal.valueOf(total);

                int comparisonResult = doubleAsBigDecimal.compareTo(voucher.getMinPurchaseAmount());
                if (comparisonResult < 0) {
                    errors.add(localizationUtils.getLocalizedMessage(MessageKeys.CONDITION_USING_VOUCHER));
                }
            }
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_VOUCHER_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .build());
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_VOUCHER_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(VoucherResponse.fromVoucher(voucher)).build());
    }

    @GetMapping("/by-code/phone-number")
    public ResponseEntity<ResponseObject> getVoucherByVoucherCodeWithPhoneNumber(@RequestParam String code,
                                                                  @RequestParam String phoneNumber) {
        List<String> errors = new ArrayList<>();
        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_CODE_IS_REQUIRED))
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }

        Voucher voucher = voucherService.getVoucherByCode(code);

        if (voucher == null) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_IS_NOT_EXISTS))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .build());
        }

        if (LocalDate.now().isBefore(voucher.getStartDate())) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_HAS_NOT_APPLIED));
        }

        if (LocalDate.now().isAfter(voucher.getEndDate())) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_IS_EXPIRED));
        }


        if (voucher.getMaxUsage() != null && voucher.getTimesUsed() >= voucher.getMaxUsage()) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_IS_MAX_USAGE));
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_VOUCHER_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .build());
        }

        if (!Objects.isNull(phoneNumber) && StringUtils.isNotBlank(phoneNumber)) {
            Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
            if (user.isPresent()) {
                List<Order> orders = orderRepository.findByUserId(user.get().getId());
                double total = 0.0;
                for (Order order : orders) {
                    total += order.getOrderDetails().stream().mapToDouble(e -> e.getPrice() * e.getNumberOfProduct()).sum();
                }
                BigDecimal doubleAsBigDecimal = BigDecimal.valueOf(total);

                int comparisonResult = doubleAsBigDecimal.compareTo(voucher.getMinPurchaseAmount());
                if (comparisonResult < 0) {
                    errors.add(localizationUtils.getLocalizedMessage(MessageKeys.CONDITION_USING_VOUCHER));
                }
            }
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseObject.builder()
                    .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_VOUCHER_FAILED))
                    .status(HttpStatus.BAD_REQUEST)
                    .data(errors)
                    .build());
        }

        return ResponseEntity.ok().body(ResponseObject.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.GET_VOUCHER_SUCCESSFULLY))
                .status(HttpStatus.OK)
                .data(VoucherResponse.fromVoucher(voucher)).build());
    }
}
