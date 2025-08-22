package ru.rpovetkin.paymentservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.rpovetkin.paymentservice.conf.PaymentProperties;
import ru.rpovetkin.paymentservice.web.PaymentController;

@WebFluxTest(controllers = PaymentController.class)
@Import({PaymentProperties.class})
class PaymentControllerSecurityTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void whenNoAuthorizationHeader_then401() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/payment").queryParam("amountForPay", "10").build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void whenSecurityDisabledInTestConfig_then200() {
        WebTestClient unsecuredClient = WebTestClient.bindToController(new PaymentController(new PaymentProperties()))
                .webFilter(((exchange, chain) -> chain.filter(exchange)))
                .configureClient()
                .build();

        unsecuredClient.get()
                .uri(uriBuilder -> uriBuilder.path("/payment").queryParam("amountForPay", "10").build())
                .header(HttpHeaders.AUTHORIZATION, "")
                .exchange()
                .expectStatus().isOk();
    }

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        public SecurityWebFilterChain testSecurityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(ex -> ex.anyExchange().denyAll())
                    .build();
        }
    }
}
