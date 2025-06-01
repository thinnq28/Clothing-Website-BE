package com.datn.shop_app.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
@RequiredArgsConstructor

public class Config {

    @Value("${payos.PAYOS_CLIENT_ID}")
    private String clientId;

    @Value("${payos.PAYOS_API_KEY}")
    private String apiKey;

    @Value("${payos.PAYOS_CHECKSUM_KEY}")
    private String checksumKey;

    @Bean
    public PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}
