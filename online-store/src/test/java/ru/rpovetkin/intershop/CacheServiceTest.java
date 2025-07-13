package ru.rpovetkin.intershop;

import org.junit.jupiter.api.Test;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.ItemCardDto;
import ru.rpovetkin.intershop.model.ItemListDto;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CacheServiceTest {

    @Test
    void testItemCardDtoCreation() {
        Item item = new Item(1L, "Test Item", "Test Description", "/test.jpg", 5, new BigDecimal("100.00"));
        ItemCardDto dto = new ItemCardDto(item.getId(), item.getImgPath(), item.getTitle(), item.getPrice(), item.getDescription());
        
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getImgPath(), dto.getImgPath());
        assertEquals(item.getTitle(), dto.getTitle());
        assertEquals(item.getPrice(), dto.getPrice());
        assertEquals(item.getDescription(), dto.getDescription());
    }

    @Test
    void testItemListDtoCreation() {
        Item item = new Item(1L, "Test Item", "Test Description", "/test.jpg", 5, new BigDecimal("100.00"));
        ItemListDto dto = new ItemListDto(item.getId(), item.getTitle(), item.getDescription(), item.getPrice());
        
        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getTitle(), dto.getTitle());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.getPrice(), dto.getPrice());
    }

    @Test
    void testDtoSerialization() {
        ItemCardDto cardDto = new ItemCardDto(1L, "/test.jpg", "Test Item", new BigDecimal("100.00"), "Test Description");
        ItemListDto listDto = new ItemListDto(1L, "Test Item", "Test Description", new BigDecimal("100.00"));
        
        // Проверяем, что DTO можно создать и они содержат правильные данные
        assertNotNull(cardDto);
        assertNotNull(listDto);
        assertEquals(1L, cardDto.getId());
        assertEquals(1L, listDto.getId());
    }
} 