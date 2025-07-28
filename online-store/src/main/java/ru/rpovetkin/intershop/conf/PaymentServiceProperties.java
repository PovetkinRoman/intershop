package ru.rpovetkin.intershop.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payment.service")
public class PaymentServiceProperties {
    
    /**
     * Базовый URL платежного сервиса
     */
    private String baseUrl = "http://payment-service-app:8080";
    
    /**
     * Путь к API платежей
     */
    private String paymentPath = "/payment";
    
    /**
     * Полный URL для API платежей
     */
    public String getPaymentUrl() {
        return baseUrl + paymentPath;
    }
} 