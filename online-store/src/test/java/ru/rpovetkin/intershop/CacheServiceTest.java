package ru.rpovetkin.intershop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.ItemCardDto;
import ru.rpovetkin.intershop.model.ItemListDto;
import ru.rpovetkin.intershop.service.CacheService;
import ru.rpovetkin.intershop.service.ItemMapperService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CacheServiceTest {

    @Mock
    private ItemMapperService itemMapperService;

    private CacheService cacheService;
    private Item testItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cacheService = new CacheService(itemMapperService);
        testItem = new Item(1L, "Test Item", "Test Description", "/test.jpg", 5, new BigDecimal("100.00"));
    }

    @Test
    void testCacheItemCard() {
        ItemCardDto expectedDto = new ItemCardDto(1L, "/test.jpg", "Test Item", new BigDecimal("100.00"), "Test Description");
        when(itemMapperService.toItemCardDto(testItem)).thenReturn(expectedDto);

        ItemCardDto result = cacheService.cacheItemCard(testItem);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(itemMapperService).toItemCardDto(testItem);
    }

    @Test
    void testCacheItemList() {
        ItemListDto expectedDto = new ItemListDto(1L, "Test Item", "Test Description", new BigDecimal("100.00"));
        when(itemMapperService.toItemListDto(testItem)).thenReturn(expectedDto);

        ItemListDto result = cacheService.cacheItemList(testItem);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(itemMapperService).toItemListDto(testItem);
    }

    @Test
    void testCacheAllItemsList() {
        List<Item> items = Arrays.asList(testItem);
        List<ItemListDto> expectedDtos = Arrays.asList(
            new ItemListDto(1L, "Test Item", "Test Description", new BigDecimal("100.00"))
        );
        when(itemMapperService.toItemListDtoList(items)).thenReturn(expectedDtos);

        List<ItemListDto> result = cacheService.cacheAllItemsList(items);

        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(itemMapperService).toItemListDtoList(items);
    }

    @Test
    void testEvictItemCard() {
        // Метод evictItemCard не должен выбрасывать исключения
        assertDoesNotThrow(() -> cacheService.evictItemCard(1L));
    }

    @Test
    void testEvictItemList() {
        // Метод evictItemList не должен выбрасывать исключения
        assertDoesNotThrow(() -> cacheService.evictItemList(1L));
    }

    @Test
    void testEvictAllItemsList() {
        // Метод evictAllItemsList не должен выбрасывать исключения
        assertDoesNotThrow(() -> cacheService.evictAllItemsList());
    }

    @Test
    void testEvictAllItemCaches() {
        // Метод evictAllItemCaches не должен выбрасывать исключения
        assertDoesNotThrow(() -> cacheService.evictAllItemCaches(1L));
    }
} 