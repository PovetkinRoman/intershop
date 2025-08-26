package ru.rpovetkin.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.service.ItemService;
import ru.rpovetkin.intershop.service.OrderService;
import ru.rpovetkin.intershop.web.CartController;
import ru.rpovetkin.intershop.web.MainItemController;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest({MainItemController.class, CartController.class})
@Import({OnlineStoreSecurityAnonymousTest.TestSecurityChain.class, OnlineStoreSecurityAnonymousTest.TestBeans.class})
class OnlineStoreSecurityAnonymousTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    ItemService itemService;

    @MockitoBean
    OrderService orderService;

    @Test
    void anonymous_canGetMainAndItem() {
        List<Item> items = List.of(
                new Item(1L, "Футболка", "Хлопковая футболка", "cap.jpg", 0, BigDecimal.valueOf(1999)),
                new Item(2L, "Джинсы", "Синие джинсы", "slippers.jpg", 0, BigDecimal.valueOf(3999))
        );
        when(itemService.findAllWithPagination(any(), anyString())).thenReturn(Flux.fromIterable(items));

        Item item = new Item(1L, "Кроссовки", "Спортивные кроссовки", "sneakers.jpg", 0, BigDecimal.valueOf(4999.99));
        when(itemService.findById(1L)).thenReturn(Mono.just(item));

        webTestClient.get().uri("/main/items?sort=PRICE&pageNumber=1&pageSize=5")
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri("/main/items/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void anonymous_postAndCart_areRedirectedToLogin() {
        when(itemService.changeCountItemsReactive(eq(1L), anyString())).thenReturn(Mono.empty());

        webTestClient.post().uri("/main/items/1")
                .bodyValue("action=PLUS")
                .exchange()
                .expectStatus().is3xxRedirection();

        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().is3xxRedirection();

        webTestClient.post().uri("/cart/items/1")
                .bodyValue("action=PLUS")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @TestConfiguration
    static class TestSecurityChain {
        @Bean
        public SecurityWebFilterChain testSecurityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .authorizeExchange(ex -> ex
                            .pathMatchers(HttpMethod.GET, "/main/**", "/", "/items/**").permitAll()
                            .pathMatchers("/cart/**", "/orders/**").authenticated()
                            .pathMatchers(HttpMethod.POST, "/items/**", "/main/**").authenticated()
                            .anyExchange().authenticated()
                    )
                    .formLogin(form -> form.loginPage("/login"))
                    .build();
        }
    }

    @TestConfiguration
    static class TestBeans {
        @Bean
        public org.springframework.web.reactive.function.client.WebClient paymentServiceWebClient() {
            return org.springframework.web.reactive.function.client.WebClient.builder()
                    .baseUrl("http://localhost")
                    .build();
        }
    }
}
