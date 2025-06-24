package ru.rpovetkin.intershop.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.Action;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.repository.ItemRepository;

import java.util.Comparator;

import static ru.rpovetkin.intershop.model.Action.*;

@Service
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

//    public Mono<Void> changeCountItems(Long id, String action) {
//        return changeCountItemsReactive(id, action).then();
//    }

//    public Mono<Item> changeCountItemsReactive(Long id, String action) {
//        Action actionEnum;
//        try {
//            actionEnum = Action.valueOf(action.trim().toUpperCase());
//        } catch (IllegalArgumentException e) {
//            return Mono.error(new IllegalStateException("Invalid action: " + action));
//        }
//
//        return itemRepository.findById(id)
//                .switchIfEmpty(Mono.error(new IllegalArgumentException("Item not found")))
//                .flatMap(item -> {
//                    switch (actionEnum) {
//                        case DELETE: item.setCount(0); break;
//                        case PLUS: item.setCount(item.getCount() + 1); break;
//                        case MINUS: item.setCount(Math.max(item.getCount() - 1, 0)); break;
//                    }
//                    return itemRepository.save(item);
//                });
//    }

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
                        case DELETE: item.setCount(0); break;
                        case PLUS: item.setCount(item.getCount() + 1); break;
                        case MINUS: item.setCount(Math.max(item.getCount() - 1, 0)); break;
                    }
                    return itemRepository.save(item);
                })
                .then();
    }


//    public Flux<Item> findAllWithPagination(Pageable pageable, String search) {
//        Flux<Item> items = itemRepository.findAllBy(pageable);
//
//        if (!search.isEmpty()) {
//            return items
//                    .filter(i -> i.getTitle().toLowerCase().contains(search.toLowerCase()))
//                    .doOnComplete(() -> log.debug("Filtered pagination completed"));
//        }
//
//        return items
//                .doOnComplete(() -> log.debug("Pagination without filter completed"));
//    }

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
                .then(); // Игнорируем количество измененных строк и возвращаем Mono<Void>
    }

    public Mono<Item> findById(Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Item not found with id: " + id)));
    }
}
