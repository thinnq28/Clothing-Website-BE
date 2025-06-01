package com.datn.shop_app.schedule;

import com.datn.shop_app.entity.Voucher;
import com.datn.shop_app.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduleTasks {
    private final VoucherService voucherService;

    @Scheduled(cron = "59 59 23 * * ?")
    public void deleteVoucher(){
        List<Voucher> vouchers = voucherService.getVouchers(LocalDate.now());
        if(vouchers != null && !vouchers.isEmpty()) {
            voucherService.setActive(vouchers);
        }
    }

}
