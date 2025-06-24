package ru.rpovetkin.intershop.web;

import lombok.extern.slf4j.Slf4j;
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
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/{id}")
    public Mono<Rendering> changeItem(@PathVariable Long id, ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    String action = formData.getFirst("action");
                    log.info("changeItem: id={}, action={}", id, action);

                    return itemService.changeCountItemsReactive(id, action)
                            .then(Mono.just(Rendering.redirectTo("/items/{id}")
                                    .modelAttribute("id", id)
                                    .build()));
                });
    }


    @GetMapping("/{id}")
    public Mono<Rendering> showItem(@PathVariable Long id) {
        return itemService.findById(id)
                .map(item -> Rendering.view("item")
                        .modelAttribute("item", item)
                        .build())
                .doOnNext(rendering -> log.debug("Rendering view for item id: {}", id))
                .doOnError(e -> log.error("Error fetching item: {}", e.getMessage()));
    }
}
