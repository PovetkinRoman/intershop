package ru.rpovetkin.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.service.ItemService;
import ru.rpovetkin.intershop.web.MainItemController;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(MainItemController.class)
class MainItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    ItemService itemService;

    @Test
    void showItems_get_item_id_1() {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("тапки");
        item.setCount(0);
        item.setPrice(BigDecimal.TEN);
        item.setDescription("обычные тапки");
        item.setImgPath("slippers.jpg");

        when(itemService.findById(1L)).thenReturn(Mono.just(item));

        webTestClient.get().uri("/main/items/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody());
                    assertThat(responseBody).contains("тапки");
                    assertThat(responseBody).contains("обычные тапки");
                    assertThat(responseBody).contains("slippers.jpg");
                    assertThat(responseBody).contains("10");
                });
    }

    @Test
    void testFindByIdNotFound() {
        when(itemService.findById(2L))
                .thenReturn(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found")));

        webTestClient.get().uri("/main/items/2")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void showItems_getItemById_shouldReturnItemDetails() {
        Item testItem = new Item();
        testItem.setId(1L);
        testItem.setTitle("Кроссовки");
        testItem.setDescription("Спортивные кроссовки");
        testItem.setImgPath("sneakers.jpg");
        testItem.setCount(10);
        testItem.setPrice(BigDecimal.valueOf(4999.99));

        when(itemService.findById(1L)).thenReturn(Mono.just(testItem));

        webTestClient.get().uri("/main/items/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody(), StandardCharsets.UTF_8);

                    // Проверяем наличие всех ожидаемых данных в HTML
                    assertThat(responseBody).contains("Кроссовки");
                    assertThat(responseBody).contains("Спортивные кроссовки");
                    assertThat(responseBody).contains("sneakers.jpg");
                    assertThat(responseBody).contains("10");
                    assertThat(responseBody).contains("4999.99");
                });
    }

    @Test
    void getItems_withSortingAndPagination_shouldReturnCorrectView() {
        List<Item> items = Arrays.asList(
                new Item(1L, "Футболка", "Хлопковая футболка", "image/cap.jpg", 20, BigDecimal.valueOf(1999.99)),
                new Item(2L, "Джинсы", "Синие джинсы", "image/slippers.jpg", 15, BigDecimal.valueOf(3999.99))
        );

        when(itemService.findAllWithPagination(any(Pageable.class), anyString()))
                .thenReturn(Flux.fromIterable(items));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("sort", "PRICE")
                        .queryParam("pageNumber", "1")
                        .queryParam("pageSize", "5")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody(), StandardCharsets.UTF_8);

                    // Проверка содержимого
                    assertThat(responseBody).contains("Футболка");
                    assertThat(responseBody).contains("Джинсы");
                    assertThat(responseBody).contains("1999.99");
                    assertThat(responseBody).contains("3999.99");
                });
    }

    @Test
    void changeItem_withPlusAction_shouldIncrementCount() {
        Long itemId = 1L;

        when(itemService.changeCountItemsReactive(eq(itemId), eq("PLUS")))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/main/items/{id}", itemId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("action=PLUS")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");

        verify(itemService).changeCountItemsReactive(itemId, "PLUS");
    }

    @Test
    void changeItem_withMinusAction_shouldDecrementCount() {
        Long itemId = 1L;

        when(itemService.changeCountItemsReactive(eq(itemId), eq("MINUS")))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/main/items/{id}", itemId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("action=MINUS")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");

        verify(itemService).changeCountItemsReactive(itemId, "MINUS");
    }

    @Test
    void changeItem_withDeleteAction_shouldRemoveItem() {
        Long itemId = 1L;

        when(itemService.changeCountItemsReactive(eq(itemId), eq("DELETE")))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/main/items/{id}", itemId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("action=DELETE")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/main/items");

        verify(itemService).changeCountItemsReactive(itemId, "DELETE");
    }

    @Test
    void getItems_withSearchTerm_shouldFilterResults() {
        // Arrange
        String searchTerm = "джинсы";
        Item filteredItem = new Item(2L, "Джинсы", "Синие джинсы", "jeans.jpg", 15, BigDecimal.valueOf(3999.99));
        List<Item> filteredItems = List.of(filteredItem);

        when(itemService.findAllWithPagination(any(Pageable.class), eq(searchTerm)))
                .thenReturn(Flux.fromIterable(filteredItems));

        // Act & Assert
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/main/items")
                        .queryParam("search", searchTerm)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody(), StandardCharsets.UTF_8);

                    // Проверяем что найденный товар присутствует в HTML
                    assertThat(responseBody).contains("Джинсы");
                    assertThat(responseBody).contains("Синие джинсы");
                    assertThat(responseBody).contains("3999.99");

                    // Проверяем что search term сохранился в форме
                    assertThat(responseBody).containsPattern("value=\"" + searchTerm + "\"");
                });

        // Проверяем вызов сервиса с правильными параметрами
        verify(itemService).findAllWithPagination(any(Pageable.class), eq(searchTerm));
    }
}