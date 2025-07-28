package ru.rpovetkin.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.ItemCardDto;
import ru.rpovetkin.intershop.model.ItemListDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    public static final String ITEM_CARD_CACHE = "item-card";
    public static final String ITEM_LIST_CACHE = "item-list";
    public static final String ITEM_LIST_ALL_CACHE = "item-list-all";

    private final ItemMapperService itemMapperService;

    /**
     * Кеширует карточку товара
     */
    @Cacheable(value = ITEM_CARD_CACHE, key = "#item.id")
    public ItemCardDto cacheItemCard(Item item) {
        log.debug("Caching item card for id: {}", item.getId());
        return itemMapperService.toItemCardDto(item);
    }

    /**
     * Кеширует данные для списка товаров
     */
    @Cacheable(value = ITEM_LIST_CACHE, key = "#item.id")
    public ItemListDto cacheItemList(Item item) {
        log.debug("Caching item list data for id: {}", item.getId());
        return itemMapperService.toItemListDto(item);
    }

    /**
     * Кеширует список всех товаров для списка
     */
    @Cacheable(value = ITEM_LIST_ALL_CACHE, key = "'all'")
    public List<ItemListDto> cacheAllItemsList(List<Item> items) {
        log.debug("Caching all items list, count: {}", items.size());
        return itemMapperService.toItemListDtoList(items);
    }

    /**
     * Очищает кеш карточки товара
     */
    @CacheEvict(value = ITEM_CARD_CACHE, key = "#itemId")
    public void evictItemCard(Long itemId) {
        log.debug("Evicting item card cache for id: {}", itemId);
    }

    /**
     * Очищает кеш данных для списка товаров
     */
    @CacheEvict(value = ITEM_LIST_CACHE, key = "#itemId")
    public void evictItemList(Long itemId) {
        log.debug("Evicting item list cache for id: {}", itemId);
    }

    /**
     * Очищает кеш всех товаров
     */
    @CacheEvict(value = ITEM_LIST_ALL_CACHE, key = "'all'")
    public void evictAllItemsList() {
        log.debug("Evicting all items list cache");
    }

    /**
     * Очищает все кеши для товара
     */
    public void evictAllItemCaches(Long itemId) {
        evictItemCard(itemId);
        evictItemList(itemId);
        evictAllItemsList();
    }
} 