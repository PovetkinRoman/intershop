package ru.rpovetkin.intershop.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.ItemCardDto;
import ru.rpovetkin.intershop.model.ItemListDto;
import ru.rpovetkin.intershop.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
public class ItemApiController {

    private final ItemService itemService;

    /**
     * Получить карточку товара (кешированную)
     */
    @GetMapping("/{id}/card")
    public Mono<ResponseEntity<ItemCardDto>> getItemCard(@PathVariable Long id) {
        return itemService.getCachedItemCard(id)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.debug("Retrieved cached item card for id: {}", id))
                .doOnError(e -> log.error("Error retrieving cached item card for id {}: {}", id, e.getMessage()));
    }

    /**
     * Получить данные товара для списка (кешированные)
     */
    @GetMapping("/{id}/list")
    public Mono<ResponseEntity<ItemListDto>> getItemList(@PathVariable Long id) {
        return itemService.getCachedItemList(id)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.debug("Retrieved cached item list data for id: {}", id))
                .doOnError(e -> log.error("Error retrieving cached item list data for id {}: {}", id, e.getMessage()));
    }

    /**
     * Получить все товары для списка (кешированные)
     */
    @GetMapping("/list")
    public Mono<ResponseEntity<List<ItemListDto>>> getAllItemsList() {
        return itemService.getCachedAllItemsList()
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.debug("Retrieved cached all items list, count: {}", response.getBody().size()))
                .doOnError(e -> log.error("Error retrieving cached all items list: {}", e.getMessage()));
    }

    /**
     * Получить товары с пагинацией и поиском (кешированные)
     */
    @GetMapping("/search")
    public Mono<ResponseEntity<List<ItemListDto>>> searchItems(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "sort", defaultValue = "ALPHA") String sort,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        log.debug("Search items: search={}, page={}, size={}, sort={}", search, page, size, sort);

        Sort sorting = Sort.unsorted();
        if ("ALPHA".equals(sort)) {
            sorting = Sort.by("title").ascending();
        } else if ("PRICE".equals(sort)) {
            sorting = Sort.by("price").ascending();
        }

        Pageable pageable = PageRequest.of(page, size, sorting);

        return itemService.getCachedItemsWithPagination(pageable, search)
                .collectList()
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.debug("Retrieved cached search results, count: {}", response.getBody().size()))
                .doOnError(e -> log.error("Error retrieving cached search results: {}", e.getMessage()));
    }

    /**
     * Получить товары с пагинацией (кешированные)
     */
    @GetMapping
    public Mono<ResponseEntity<List<ItemListDto>>> getItems(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", defaultValue = "ALPHA") String sort) {

        return searchItems("", sort, page, size);
    }
} 