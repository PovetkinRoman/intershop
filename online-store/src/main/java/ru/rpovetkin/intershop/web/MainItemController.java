package ru.rpovetkin.intershop.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.ItemSort;
import ru.rpovetkin.intershop.model.Paging;
import ru.rpovetkin.intershop.service.ItemService;

@Controller
@Slf4j
@RequestMapping("/main/items")
@RequiredArgsConstructor
public class MainItemController {

    private final ItemService itemService;

    @GetMapping
    public Mono<Rendering> getItems(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "sort", defaultValue = "ALPHA") String sort,
            @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        log.debug("getItems: search={}, pageNumber={}, pageSize={}, sort={}",
                search, pageNumber, pageSize, ItemSort.valueOf(sort));

        Sort sorting = Sort.unsorted();
        if ("ALPHA".equals(sort)) {
            sorting = Sort.by("title").ascending();
        } else if ("PRICE".equals(sort)) {
            sorting = Sort.by("price").ascending();
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sorting);

        return itemService.findAllWithPagination(pageable, search)
                .collectList()
                .map(items -> {
                    Paging paging = new Paging(
                            pageNumber,
                            pageSize,
                            false,
                            false
                    );

                    return Rendering.view("main")
                            .modelAttribute("items", items)
                            .modelAttribute("paging", paging)
                            .modelAttribute("search", search)
                            .modelAttribute("sort", sort)
                            .build();
                });
    }

    @PostMapping("/{id}")
    public Mono<Rendering> changeItem(@PathVariable Long id, ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    String action = formData.getFirst("action");
                    log.debug("changeItem: id={}, action={}", id, action);

                    return itemService.changeCountItemsReactive(id, action)
                            .then(Mono.just(Rendering.redirectTo("/main/items")
                                    .build()));
                });
    }

    @GetMapping("/{id}")
    public Mono<String> showItems(@PathVariable(name = "id") Long id, Model model) {
        return itemService.findById(id)
                .doOnNext(item -> model.addAttribute("item", item))
                .map(item -> "item");
    }
}
