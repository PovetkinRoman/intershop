package ru.rpovetkin.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.Action;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.ItemCardDto;
import ru.rpovetkin.intershop.model.ItemListDto;
import ru.rpovetkin.intershop.repository.ItemRepository;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CacheService cacheService;
    private final ItemMapperService itemMapperService;

    public Mono<Void> changeCountItemsReactive(Long id, String action) {
        Action actionEnum;
        try {
            actionEnum = Action.valueOf(action.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalStateException("Invalid action: " + action));
        }

        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Item not found")))
                .flatMap(item -> {
                    switch (actionEnum) {
                        case DELETE:
                            item.setCount(0);
                            break;
                        case PLUS:
                            item.setCount(item.getCount() + 1);
                            break;
                        case MINUS:
                            item.setCount(Math.max(item.getCount() - 1, 0));
                            break;
                    }
                    return itemRepository.save(item);
                })
                .doOnSuccess(item -> {
                    // Очищаем кеш после изменения товара
                    cacheService.evictAllItemCaches(id);
                })
                .then();
    }

    public Flux<Item> findAllWithPagination(Pageable pageable, String search) {
        return itemRepository.findAllBy(pageable)
                .filterWhen(item -> {
                    if (!search.isEmpty()) {
                        return Mono.just(item.getTitle().toLowerCase()
                                .contains(search.toLowerCase()));
                    }
                    return Mono.just(true);
                })
                .doOnComplete(() -> log.debug("Pagination completed with search: {}",
                        !search.isEmpty()));
    }

    public Flux<Item> findAllInCartSorted() {
        return itemRepository.findItemsForCart()
                .sort(Comparator.comparing(Item::getId));
    }

    public Mono<Void> setItemCountZeroAllInCart() {
        return itemRepository.setItemCountZeroForAllInCart()
                .doOnSuccess(v -> {
                    // Очищаем кеш всех товаров после очистки корзины
                    cacheService.evictAllItemsList();
                })
                .then();
    }

    public Mono<Item> findById(Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Item not found with id: " + id)));
    }

    /**
     * Получает кешированную карточку товара
     */
    public Mono<ItemCardDto> getCachedItemCard(Long id) {
        return findById(id)
                .map(cacheService::cacheItemCard);
    }

    /**
     * Получает карточку товара без кеширования
     */
    public Mono<ItemCardDto> getItemCard(Long id) {
        return findById(id)
                .map(itemMapperService::toItemCardDto);
    }

    /**
     * Получает кешированные данные для списка товаров
     */
    public Mono<ItemListDto> getCachedItemList(Long id) {
        return findById(id)
                .map(cacheService::cacheItemList);
    }

    /**
     * Получает данные для списка товаров без кеширования
     */
    public Mono<ItemListDto> getItemList(Long id) {
        return findById(id)
                .map(itemMapperService::toItemListDto);
    }

    /**
     * Получает кешированный список всех товаров для отображения в списке
     */
    public Mono<List<ItemListDto>> getCachedAllItemsList() {
        return itemRepository.findAll()
                .collectList()
                .map(cacheService::cacheAllItemsList);
    }

    /**
     * Получает список всех товаров без кеширования
     */
    public Mono<List<ItemListDto>> getAllItemsList() {
        return itemRepository.findAll()
                .collectList()
                .map(itemMapperService::toItemListDtoList);
    }

    /**
     * Получает кешированные данные для списка товаров с пагинацией и поиском
     */
    public Flux<ItemListDto> getCachedItemsWithPagination(Pageable pageable, String search) {
        return findAllWithPagination(pageable, search)
                .map(cacheService::cacheItemList);
    }

    /**
     * Получает данные для списка товаров с пагинацией и поиском без кеширования
     */
    public Flux<ItemListDto> getItemsWithPagination(Pageable pageable, String search) {
        return findAllWithPagination(pageable, search)
                .map(itemMapperService::toItemListDto);
    }
}
