package com.datn.shop_app.service.impl;

import com.datn.shop_app.DTO.VoucherDTO;
import com.datn.shop_app.entity.Voucher;
import com.datn.shop_app.repository.VoucherRepository;
import com.datn.shop_app.response.voucher.VoucherResponse;
import com.datn.shop_app.service.VoucherService;
import com.datn.shop_app.utils.LocalizationUtils;
import com.datn.shop_app.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {
    private final VoucherRepository voucherRepository;
    private final LocalizationUtils localizationUtils;

    @Override
    public Page<VoucherResponse> getAllVouchers(String code, LocalDate startDate, LocalDate endDate, Boolean active, Pageable pageable) {
        Page<Voucher> vouchers = voucherRepository.findAllVouchers(code, startDate, endDate, active, pageable);
        return vouchers.map(VoucherResponse::fromVoucher);
    }

    @Override
    public Voucher save(VoucherDTO voucherDTO) {
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(voucherDTO, voucher);
        voucher.setActive(true);
        voucher.setTimesUsed(0);
        return voucherRepository.save(voucher);
    }

    @Override
    public List<String> validate(VoucherDTO voucherDTO, BindingResult bindingResult, boolean isCreate) {
        List<String> errors = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            errors.add(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return errors;
        }

        String discountType = voucherDTO.getDiscountType();
        if (!discountType.equalsIgnoreCase("percentage") && !discountType.equalsIgnoreCase("fixed")) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.DISCOUNT_TYPE));
        }

        if (voucherDTO.getStartDate().isAfter(voucherDTO.getEndDate())) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.START_DATE_AFTER_END_DATE));
        }

        if (discountType.equalsIgnoreCase("percentage") && voucherDTO.getDiscount().intValue() > 100) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.DISCOUNT_TYPE_PERCENTAGE));
        }

        Voucher voucher = voucherRepository.findVoucherByCode(voucherDTO.getCode());
        if (voucher != null && isCreate) {
            errors.add(localizationUtils.getLocalizedMessage(MessageKeys.VOUCHER_IS_EXISTS));
        }

        return errors;
    }

    @Override
    public Voucher update(Integer id, VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.findByIdAndActive(id, true);
        if(voucher != null) {
            BeanUtils.copyProperties(voucherDTO, voucher);
            return voucherRepository.save(voucher);
        }

        return null;
    }

    @Override
    public Voucher getVoucher(Integer id) {
        return voucherRepository.findByIdAndActive(id, true);
    }

    @Override
    public void delete(Integer id) {
        Voucher voucher = voucherRepository.findByIdAndActive(id, true);
        if(voucher != null){
            voucher.setActive(false);
            voucherRepository.save(voucher);
        }
    }

    @Override
    public List<Voucher> getVouchers(LocalDate endDate){
        return voucherRepository.getVoucherByEndDate(endDate, true);
    }

    @Override
    public void setActive(List<Voucher> vouchers){
        for (Voucher Voucher : vouchers) {
            Voucher.setActive(false);
        }
        voucherRepository.saveAll(vouchers);
    }

    @Override
    public Voucher getVoucherByCode(String code){
        return voucherRepository.findByCodeAndActive(code, true);
    }
}
