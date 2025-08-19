package ru.rpovetkin.intershop.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
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
@RequestMapping("/main/items")
@RequiredArgsConstructor
@Slf4j
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

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(auth -> itemService.findAllWithPaginationForUser(pageable, search, auth.getName())
                        .collectList()
                        .map(items -> {
                            Paging paging = new Paging(
                                    pageNumber,
                                    pageSize,
                                    false,
                                    false
                            );

                            boolean isUser = auth.isAuthenticated() && auth.getAuthorities().stream()
                                    .noneMatch(a -> "ROLE_ANONYMOUS".equals(a.getAuthority()));

                            return Rendering.view("main")
                                    .modelAttribute("items", items)
                                    .modelAttribute("paging", paging)
                                    .modelAttribute("search", search)
                                    .modelAttribute("sort", sort)
                                    .modelAttribute("isUser", isUser)
                                    .build();
                        })
                );
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Mono<Rendering> changeItem(@PathVariable Long id, ServerWebExchange exchange) {
        return Mono.zip(
                ReactiveSecurityContextHolder.getContext()
                        .map(ctx -> ctx.getAuthentication().getName()),
                exchange.getFormData()
        ).flatMap(tuple -> {
            String username = tuple.getT1();
            String action = tuple.getT2().getFirst("action");
            log.debug("changeItem: id={}, action={}", id, action);

            return itemService.changeCountItemsReactive(id, action, username)
                    .then(Mono.just(Rendering.redirectTo("/main/items").build()));
        });
    }

    @GetMapping("/{id}")
    public Mono<String> showItems(@PathVariable(name = "id") Long id, Model model) {
        Mono<Boolean> isUserMono = ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .map(auth -> auth.isAuthenticated() && auth.getAuthorities().stream()
                        .noneMatch(a -> "ROLE_ANONYMOUS".equals(a.getAuthority())))
                .defaultIfEmpty(false);

        return Mono.zip(itemService.findById(id), isUserMono)
                .doOnNext(tuple -> {
                    model.addAttribute("item", tuple.getT1());
                    model.addAttribute("isUser", tuple.getT2());
                })
                .thenReturn("item");
    }
}
