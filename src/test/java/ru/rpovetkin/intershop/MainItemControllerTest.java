package ru.rpovetkin.intershop;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.service.ItemService;
import ru.rpovetkin.intershop.web.MainItemController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainItemController.class)
@ActiveProfiles("test")
class MainItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ItemService itemService;

    @Test
    void showItems_get_item_id_1() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setTitle("тапки");
        item.setCount(0);
        item.setPrice(BigDecimal.TEN);
        item.setDescription("обычные тапки");
        item.setImgPath("slippers.jpg");
        doReturn(item).when(itemService).findById(1L);

        mockMvc.perform(get("/main/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    void testFindByIdNotFound() throws Exception {
        when(itemService.findById(2L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        mockMvc.perform(get("/main/items/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void showItems_getItemById_shouldReturnItemDetails() throws Exception {
        Item testItem = new Item(1L, "Кроссовки", "Спортивные кроссовки",
                "sneakers.jpg", 10, BigDecimal.valueOf(4999.99));

        when(itemService.findById(1L)).thenReturn(testItem);

        mockMvc.perform(get("/main/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("item", hasProperty("id", is(1L))))
                .andExpect(model().attribute("item", hasProperty("title", is("Кроссовки"))))
                .andExpect(model().attribute("item", hasProperty("description", is("Спортивные кроссовки"))))
                .andExpect(model().attribute("item", hasProperty("imgPath", is("sneakers.jpg"))))
                .andExpect(model().attribute("item", hasProperty("count", is(10))))
                .andExpect(model().attribute("item", hasProperty("price", is(BigDecimal.valueOf(4999.99)))));
    }

    @Test
    void getItems_withSortingAndPagination_shouldReturnCorrectView() throws Exception {
        List<Item> items = Arrays.asList(
                new Item(1L, "Футболка", "Хлопковая футболка", "tshirt.jpg", 20, BigDecimal.valueOf(1999.99)),
                new Item(2L, "Джинсы", "Синие джинсы", "jeans.jpg", 15, BigDecimal.valueOf(3999.99))
        );

        when(itemService.findAllWithPagination(ArgumentMatchers.any(), anyString())).thenReturn(items);

        mockMvc.perform(get("/main/items")
                        .param("sort", "PRICE")
                        .param("pageNumber", "2")
                        .param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("items", hasSize(2)))
                .andExpect(model().attribute("sort", "PRICE"))
                .andExpect(model().attribute("paging", hasProperty("pageSize", is(5))))
                .andExpect(view().name("main"));
    }

    @Test
    void changeItem_withValidActions_shouldUpdateCount() throws Exception {
        Long itemId = 1L;

        // Тестируем INCREMENT
        mockMvc.perform(post("/main/items/{id}", itemId)
                        .param("action", "INCREMENT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(itemService).changeCountItems(itemId, "INCREMENT");

        // Тестируем DECREMENT
        mockMvc.perform(post("/main/items/{id}", itemId)
                        .param("action", "DECREMENT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(itemService).changeCountItems(itemId, "DECREMENT");
    }

    @Test
    void getItems_withSearchTerm_shouldFilterResults() throws Exception {
        String searchTerm = "джинсы";
        List<Item> filteredItems = List.of(
                new Item(2L, "Джинсы", "Синие джинсы", "jeans.jpg", 15, BigDecimal.valueOf(3999.99))
        );

        when(itemService.findAllWithPagination(any(), eq(searchTerm)))
                .thenReturn(filteredItems);

        mockMvc.perform(get("/main/items")
                        .param("search", searchTerm))
                .andExpect(status().isOk())
                .andExpect(model().attribute("items", filteredItems))
                .andExpect(model().attribute("search", searchTerm));
    }
}