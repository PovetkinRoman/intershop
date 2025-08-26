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

    // OAuth2 Client Credentials for Keycloak
    private String authServerTokenUrl = "http://keycloak:8080/realms/intershop-realm/protocol/openid-connect/token";
    private String clientId = "online-store";
    private String clientSecret = "change-me";
    
    /**
     * Полный URL для API платежей
     */
    public String getPaymentUrl() {
        return baseUrl + paymentPath;
    }
} 