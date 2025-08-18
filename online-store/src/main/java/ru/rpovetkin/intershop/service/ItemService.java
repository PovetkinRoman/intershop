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
import ru.rpovetkin.intershop.model.CartItem;
import ru.rpovetkin.intershop.repository.ItemRepository;
import ru.rpovetkin.intershop.repository.CartItemRepository;
import ru.rpovetkin.intershop.repository.UserRepository;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CacheService cacheService;
    private final ItemMapperService itemMapperService;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public Mono<Void> changeCountItemsReactive(Long itemId, String action, String username) {
        Action actionEnum;
        try {
            actionEnum = Action.valueOf(action.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalStateException("Invalid action: " + action));
        }

        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> itemRepository.findById(itemId)
                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Item not found")))
                        .then(cartItemRepository.findByUserIdAndItemId(user.getId(), itemId))
                        .flatMap(existing -> {
                            switch (actionEnum) {
                                case DELETE:
                                    return cartItemRepository.deleteByUserIdAndItemId(user.getId(), itemId).then(Mono.empty());
                                case PLUS:
                                    existing.setCount(existing.getCount() + 1);
                                    return cartItemRepository.save(existing);
                                case MINUS:
                                    int next = Math.max(existing.getCount() - 1, 0);
                                    if (next == 0) {
                                        return cartItemRepository.deleteByUserIdAndItemId(user.getId(), itemId).then(Mono.empty());
                                    }
                                    existing.setCount(next);
                                    return cartItemRepository.save(existing);
                            }
                            return Mono.empty();
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            if (actionEnum == Action.PLUS) {
                                return cartItemRepository.save(new CartItem(user.getId(), itemId, 1));
                            }
                            return Mono.empty();
                        }))
                        .then())
                .doOnSuccess(v -> cacheService.evictAllItemCaches(itemId))
                .then();
    }

    // Backward-compatible overload for tests/callers without explicit username
    public Mono<Void> changeCountItemsReactive(Long itemId, String action) {
        return org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(username -> changeCountItemsReactive(itemId, action, username));
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

    public Flux<Item> findAllWithPaginationForUser(Pageable pageable, String search, String username) {
        return userRepository.findByUsername(username)
                .flatMapMany(user -> findAllWithPagination(pageable, search)
                        .flatMap(item -> cartItemRepository.findByUserIdAndItemId(user.getId(), item.getId())
                                .defaultIfEmpty(new CartItem(user.getId(), item.getId(), 0))
                                .map(ci -> {
                                    item.setCount(ci.getCount());
                                    return item;
                                })
                        )
                );
    }

    public Flux<Item> findAllInCartSorted(String username) {
        return userRepository.findByUsername(username)
                .flatMapMany(user -> cartItemRepository.findByUserId(user.getId())
                        .flatMap(cartItem -> itemRepository.findById(cartItem.getItemId())
                                .map(item -> {
                                    item.setCount(cartItem.getCount());
                                    return item;
                                }))
                        .sort(Comparator.comparing(Item::getId))
                );
    }

    // Backward-compatible overload for tests/callers without explicit username
    public Flux<Item> findAllInCartSorted() {
        return org.springframework.security.core.context.ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMapMany(this::findAllInCartSorted);
    }

    public Mono<Void> setItemCountZeroAllInCart(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> cartItemRepository.deleteByUserId(user.getId()))
                .doOnSuccess(v -> cacheService.evictAllItemsList())
                .then();
    }

    public Mono<Item> findById(Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Item not found with id: " + id)));
    }

    public Mono<Item> findByIdWithUserCount(Long id, String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> itemRepository.findById(id)
                        .switchIfEmpty(Mono.error(new RuntimeException("Item not found with id: " + id)))
                        .flatMap(item -> cartItemRepository.findByUserIdAndItemId(user.getId(), id)
                                .defaultIfEmpty(new CartItem(user.getId(), id, 0))
                                .map(ci -> {
                                    item.setCount(ci.getCount());
                                    return item;
                                })
                        )
                );
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
