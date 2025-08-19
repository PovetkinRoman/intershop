package ru.rpovetkin.intershop.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.service.ItemService;

@Controller
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

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
                    .then(Mono.just(Rendering.redirectTo("/items/{id}")
                            .modelAttribute("id", id)
                            .build()));
        });
    }

    @GetMapping("/{id}")
    public Mono<Rendering> showItem(@PathVariable Long id) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .flatMap(auth -> itemService.findByIdWithUserCount(id, auth.getName())
                        .map(item -> {
                            boolean isUser = auth.isAuthenticated() && auth.getAuthorities().stream()
                                    .noneMatch(a -> "ROLE_ANONYMOUS".equals(a.getAuthority()));
                            return Rendering.view("item")
                                    .modelAttribute("item", item)
                                    .modelAttribute("isUser", isUser)
                                    .build();
                        })
                )
                .doOnNext(rendering -> log.debug("Rendering view for item id: {}", id))
                .doOnError(e -> log.error("Error fetching item: {}", e.getMessage()));
    }
}
