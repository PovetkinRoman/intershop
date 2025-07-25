package ru.rpovetkin.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.rpovetkin.paymentservice.web.PaymentController;

@WebFluxTest(controllers = PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void checkBalance_enoughMoney_returnsTrue() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/payment").queryParam("amountForPay", "100").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void checkBalance_notEnoughMoney_returnsFalse() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/payment").queryParam("amountForPay", "10000").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);
    }

    @Test
    void payCart_enoughMoney_returnsTrueAndDecreasesBalance() {
        // Сначала убедимся, что баланс достаточен
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/payment").queryParam("amountForPay", "100").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);
    }

    @Test
    void payCart_notEnoughMoney_returnsFalse() {
        // Пытаемся оплатить большую сумму, чем на балансе
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/payment").queryParam("amountForPay", "10000").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(false);
    }
} 