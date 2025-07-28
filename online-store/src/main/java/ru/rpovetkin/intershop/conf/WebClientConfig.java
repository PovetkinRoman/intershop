package ru.rpovetkin.intershop.conf;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final PaymentServiceProperties paymentServiceProperties;

    @Bean
    public WebClient paymentServiceWebClient() {
        return WebClient.builder()
                .baseUrl(paymentServiceProperties.getPaymentUrl())
                .build();
    }
} 