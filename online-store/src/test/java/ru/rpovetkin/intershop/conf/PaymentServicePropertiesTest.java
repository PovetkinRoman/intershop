package ru.rpovetkin.intershop.conf;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PaymentServiceProperties.class)
@EnableConfigurationProperties(PaymentServiceProperties.class)
@TestPropertySource(properties = {
    "payment.service.base-url=http://test-payment-service:8080",
    "payment.service.payment-path=/api/payment"
})
class PaymentServicePropertiesTest {

    @Test
    void testPaymentServiceProperties() {
        PaymentServiceProperties properties = new PaymentServiceProperties();
        properties.setBaseUrl("http://test-payment-service:8080");
        properties.setPaymentPath("/api/payment");
        
        assertNotNull(properties);
        assertEquals("http://test-payment-service:8080", properties.getBaseUrl());
        assertEquals("/api/payment", properties.getPaymentPath());
        assertEquals("http://test-payment-service:8080/api/payment", properties.getPaymentUrl());
    }
    
    @Test
    void testDefaultValues() {
        PaymentServiceProperties properties = new PaymentServiceProperties();
        
        assertEquals("http://payment-service-app:8080", properties.getBaseUrl());
        assertEquals("/payment", properties.getPaymentPath());
        assertEquals("http://payment-service-app:8080/payment", properties.getPaymentUrl());
    }
} 