package ru.rpovetkin.intershop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.Order;
import ru.rpovetkin.intershop.service.ItemService;
import ru.rpovetkin.intershop.service.OrderService;
import ru.rpovetkin.intershop.web.CartController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@ActiveProfiles("test")
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ItemService itemService;

    @MockitoBean
    OrderService orderService;


    @Test
    void cartItems_shouldShowEmptyCart() throws Exception {
        when(itemService.findAllInCart()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("items", hasSize(0)))
                .andExpect(model().attribute("total", BigDecimal.ZERO))
                .andExpect(model().attribute("empty", true))
                .andExpect(view().name("cart"));
    }

    @Test
    void cartItems_shouldShowItemsWithTotal() throws Exception {
        List<Item> items = Arrays.asList(
                new Item(1L, "Футболка", "", "short.jpg", 2, BigDecimal.valueOf(1500)),
                new Item(2L, "Джинсы", "", "short.jpg", 1, BigDecimal.valueOf(3000))
        );
        BigDecimal expectedTotal = BigDecimal.valueOf(6000);

        when(itemService.findAllInCart()).thenReturn(items);

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("items", hasSize(2)))
                .andExpect(model().attribute("total", expectedTotal))
                .andExpect(model().attribute("empty", false))
                .andExpect(view().name("cart"));
    }

    @Test
    void cartChangeItem_shouldHandleIncrement() throws Exception {
        Long itemId = 1L;
        String action = "INCREMENT";

        mockMvc.perform(post("/cart/items/{id}", itemId)
                        .param("action", action))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(itemService).changeCountItems(itemId, action);
    }

    @Test
    void cartChangeItem_shouldHandleDecrement() throws Exception {
        Long itemId = 1L;
        String action = "DECREMENT";

        mockMvc.perform(post("/cart/items/{id}", itemId)
                        .param("action", action))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(itemService).changeCountItems(itemId, action);
    }

    @Test
    void cartBuyItems_shouldCreateOrderAndClearCart() throws Exception {
        List<Item> cartItems = List.of(
                new Item(1L, "Футболка", "", "short.jpg", 2, BigDecimal.valueOf(1500)));

        Order createdOrder = new Order();
        createdOrder.setId(100L);

        when(itemService.findAllInCart()).thenReturn(cartItems);
        when(orderService.createOrder(cartItems)).thenReturn(createdOrder);

        mockMvc.perform(post("/cart/items/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/100?newOrder=true"));

        verify(itemService).setItemCountNullAllInCart();
        verify(orderService).createOrder(cartItems);
    }
}