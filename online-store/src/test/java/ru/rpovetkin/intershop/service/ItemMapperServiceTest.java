package ru.rpovetkin.intershop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.ItemCardDto;
import ru.rpovetkin.intershop.model.ItemListDto;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperServiceTest {

    private ItemMapperService itemMapperService;
    private Item testItem;

    @BeforeEach
    void setUp() {
        itemMapperService = new ItemMapperService();
        testItem = new Item(1L, "Test Item", "Test Description", "/test.jpg", 5, new BigDecimal("100.00"));
    }

    @Test
    void testToItemCardDto() {
        ItemCardDto result = itemMapperService.toItemCardDto(testItem);

        assertNotNull(result);
        assertEquals(testItem.getId(), result.getId());
        assertEquals(testItem.getImgPath(), result.getImgPath());
        assertEquals(testItem.getTitle(), result.getTitle());
        assertEquals(testItem.getPrice(), result.getPrice());
        assertEquals(testItem.getDescription(), result.getDescription());
    }

    @Test
    void testToItemListDto() {
        ItemListDto result = itemMapperService.toItemListDto(testItem);

        assertNotNull(result);
        assertEquals(testItem.getId(), result.getId());
        assertEquals(testItem.getTitle(), result.getTitle());
        assertEquals(testItem.getDescription(), result.getDescription());
        assertEquals(testItem.getPrice(), result.getPrice());
    }

    @Test
    void testToItemListDtoList() {
        Item item1 = new Item(1L, "Item 1", "Description 1", "/img1.jpg", 1, new BigDecimal("100.00"));
        Item item2 = new Item(2L, "Item 2", "Description 2", "/img2.jpg", 2, new BigDecimal("200.00"));
        List<Item> items = Arrays.asList(item1, item2);

        List<ItemListDto> result = itemMapperService.toItemListDtoList(items);

        assertNotNull(result);
        assertEquals(2, result.size());
        
        ItemListDto dto1 = result.get(0);
        assertEquals(item1.getId(), dto1.getId());
        assertEquals(item1.getTitle(), dto1.getTitle());
        assertEquals(item1.getDescription(), dto1.getDescription());
        assertEquals(item1.getPrice(), dto1.getPrice());

        ItemListDto dto2 = result.get(1);
        assertEquals(item2.getId(), dto2.getId());
        assertEquals(item2.getTitle(), dto2.getTitle());
        assertEquals(item2.getDescription(), dto2.getDescription());
        assertEquals(item2.getPrice(), dto2.getPrice());
    }

    @Test
    void testToItemListDtoListWithEmptyList() {
        List<Item> items = Arrays.asList();
        List<ItemListDto> result = itemMapperService.toItemListDtoList(items);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
} 