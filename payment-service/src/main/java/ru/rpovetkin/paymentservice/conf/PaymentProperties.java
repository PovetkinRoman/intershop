package ru.rpovetkin.paymentservice.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {
    
    private BigDecimal initialBalance = new BigDecimal("1000.00");
} 