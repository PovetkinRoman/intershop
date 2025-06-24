//package ru.rpovetkin.intershop;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import ru.rpovetkin.intershop.model.Item;
//import ru.rpovetkin.intershop.repository.ItemRepository;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Slf4j
//public class ItemRepositoryIntegrationTest {
//
//    @Autowired
//    ItemRepository itemRepository;
//
//
//    @BeforeEach
//    void setUp() {
//        itemRepository.deleteAll();
//    }
//
//    @Test
//    void save_shouldAddPostToDatabase() {
//        Item testItem1 = new Item("Кроссовки", "Спортивные кроссовки",
//                "sneakers.jpg", 5, BigDecimal.valueOf(9999.99));
//        Item testItem2 = new Item( "Тапки", "Тапки",
//                "slippers.jpg", 3, BigDecimal.valueOf(999.99));
//        Item testItem3 = new Item( "Кепка", "Кепка",
//                "cap.jpg", 1, BigDecimal.valueOf(1999.99));
//        Item saved1 = itemRepository.save(testItem1);
//        Item saved2 = itemRepository.save(testItem2);
//        Item saved3 = itemRepository.save(testItem3);
//        log.info("Save 1: {}", saved1.getId());
//        log.info("Save 2: {}", saved2.getId());
//        log.info("Save 3: {}", saved3.getId());
//        Item item = itemRepository.findById(saved1.getId()).get();
//
//        assertNotNull(item);
//        assertEquals("Кроссовки", item.getTitle());
//        assertEquals(BigDecimal.valueOf(9999.99), item.getPrice());
//    }
//
//    @Test
//    void findItemsForCart_get_with_count_not_zero() {
//        // Подготовка тестовых данных
//        Item item = new Item("Кроссовки", "Спортивные кроссовки",
//                "sneakers.jpg", 5, BigDecimal.valueOf(9999.99));
//        Item item1 = new Item("Тапки", "Домашние тапки",
//                "slippers.jpg", 3, BigDecimal.valueOf(999.99));
//        Item item2 = new Item("Кепка", "Бейсболка",
//                "cap.jpg", 0, BigDecimal.valueOf(1999.99)); // Количество = 0
//
//        itemRepository.save(item);
//        itemRepository.save(item1);
//        itemRepository.save(item2);
//
//        // Выполнение метода
//        List<Item> items = itemRepository.findItemsForCart();
//        log.info("Найдено {} товаров в наличии", items.size());
//
//        // Проверки
//        assertEquals(2, items.size(), "Должно вернуть 2 товара с количеством > 0");
//        assertTrue(items.stream().anyMatch(i -> i.getTitle().equals("Кроссовки")));
//        assertTrue(items.stream().anyMatch(i -> i.getTitle().equals("Тапки")));
//        assertFalse(items.stream().anyMatch(i -> i.getTitle().equals("Кепка")),
//                "Товар с количеством 0 не должен возвращаться");
//    }
//
//    @Test
//    void findItemsForCart_shouldReturnEmptyListWhenNoItemsAvailable() {
//        // Arrange
//        Item item1 = new Item("T-shirt", "Cotton t-shirt",
//                "t-shirt.jpg", 0, BigDecimal.valueOf(1499.99));
//        Item item2 = new Item("Jeans", "Blue jeans",
//                "jeans.jpg", 0, BigDecimal.valueOf(4999.99));
//
//        itemRepository.save(item1);
//        itemRepository.save(item2);
//
//        // Act
//        List<Item> result = itemRepository.findItemsForCart();
//
//        // Assert
//        assertTrue(result.isEmpty(), "Should return empty list when no items are available");
//    }
//
//    @Test
//    void findItemsForCart_shouldHandleNegativeQuantities() {
//        // Arrange (in case business logic allows negative quantities)
//        Item item = new Item("Belt", "Leather belt",
//                "belt.jpg", -1, BigDecimal.valueOf(1999.99));
//
//        itemRepository.save(item);
//
//        // Act
//        List<Item> result = itemRepository.findItemsForCart();
//
//        // Assert
//        assertTrue(result.isEmpty(), "Items with negative quantity should not be returned");
//    }
//
//    @Test
//    void findItemsForCart_shouldReturnSingleItemWhenOnlyOneAvailable() {
//        // Arrange
//        Item availableItem = new Item("Watch", "Smart watch",
//                "watch.jpg", 1, BigDecimal.valueOf(12999.99));
//        Item outOfStockItem = new Item("Glasses", "Sunglasses",
//                "glasses.jpg", 0, BigDecimal.valueOf(5999.99));
//
//        itemRepository.save(availableItem);
//        itemRepository.save(outOfStockItem);
//
//        // Act
//        List<Item> result = itemRepository.findItemsForCart();
//
//        // Assert
//        assertEquals(1, result.size(), "Should return exactly one available item");
//        assertEquals("Watch", result.get(0).getTitle());
//    }
//
//
//}
