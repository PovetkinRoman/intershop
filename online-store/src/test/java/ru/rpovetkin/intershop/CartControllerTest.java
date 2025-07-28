package ru.rpovetkin.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.conf.PaymentServiceProperties;
import ru.rpovetkin.intershop.conf.WebClientConfig;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.service.ItemService;
import ru.rpovetkin.intershop.service.OrderService;
import ru.rpovetkin.intershop.web.CartController;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(CartController.class)
@Import({WebClientConfig.class, PaymentServiceProperties.class})
@TestPropertySource(properties = {
    "payment.service.base-url=http://localhost:8081"
})
class CartControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    ItemService itemService;

    @MockitoBean
    OrderService orderService;

    @Test
    void cartItems_shouldShowEmptyCart() {
        when(itemService.findAllInCartSorted())
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody(), StandardCharsets.UTF_8);

                    // Проверяем основные элементы пустой корзины
                    assertThat(responseBody).contains("Корзина товаров"); // Проверяем заголовок страницы
                    assertThat(responseBody).contains("Итого: 0 руб."); // Проверяем общую сумму
                    assertThat(responseBody).doesNotContain("cart-item"); // Убеждаемся, что нет товаров

                    // Проверяем наличие навигационных ссылок
                    assertThat(responseBody).contains("ГЛАВНАЯ");
                    assertThat(responseBody).contains("ЗАКАЗЫ");
                });

        verify(itemService).findAllInCartSorted();
    }

    @Test
    void cartItems_shouldShowItemsWithTotal() {
        List<Item> items = Arrays.asList(
                new Item(1L, "Футболка", "", "short.jpg", 2, BigDecimal.valueOf(1500)),
                new Item(2L, "Джинсы", "", "short.jpg", 1, BigDecimal.valueOf(3000))
        );

        when(itemService.findAllInCartSorted())
                .thenReturn(Flux.fromIterable(items));

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody(), StandardCharsets.UTF_8);

                    // Проверяем товары в корзине (точные совпадения)
                    assertThat(responseBody).contains(">Футболка</b>");
                    assertThat(responseBody).contains(">1500 руб.</b>");
                    assertThat(responseBody).contains(">Джинсы</b>");
                    assertThat(responseBody).contains(">3000 руб.</b>");

                    // Проверяем количество товаров
                    assertThat(responseBody).contains("<span>2</span>");
                    assertThat(responseBody).contains("<span>1</span>");

                    // Проверяем общую сумму
                    assertThat(responseBody).contains(">Итого: 6000 руб.</b>");

                });

        // Проверяем вызов сервиса
        verify(itemService).findAllInCartSorted();
    }

    @Test
    void cartChangeItem_shouldProcessIncrementAction() {
        Long itemId = 1L;
        String action = "PLUS";

        when(itemService.changeCountItemsReactive(eq(itemId), eq(action)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/cart/items/{id}", itemId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("action=" + action)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(itemService).changeCountItemsReactive(itemId, action);
    }

    @Test
    void cartChangeItem_shouldProcessDecrementAction() {
        Long itemId = 2L;
        String action = "MINUS";

        when(itemService.changeCountItemsReactive(eq(itemId), eq(action)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/cart/items/{id}", itemId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("action=" + action)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");
    }

    @Test
    void cartChangeItem_shouldProcessDeleteAction() {
        Long itemId = 3L;
        String action = "DELETE";

        when(itemService.changeCountItemsReactive(eq(itemId), eq(action)))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/cart/items/{id}", itemId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("action=" + action)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");
    }

    @Test
    void cartChangeItem_shouldReturnServerErrorWhenServiceFails() {
        Long itemId = 5L;
        String action = "PLUS";

        when(itemService.changeCountItemsReactive(eq(itemId), eq(action)))
                .thenReturn(Mono.error(new RuntimeException("Service error")));

        webTestClient.post()
                .uri("/cart/items/{id}", itemId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("action=" + action)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}