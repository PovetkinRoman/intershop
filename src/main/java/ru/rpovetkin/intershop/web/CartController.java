package ru.rpovetkin.intershop.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.service.ItemService;
import ru.rpovetkin.intershop.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart/items")
@Slf4j
@RequiredArgsConstructor
public class CartController {

    private final ItemService itemService;
    private final OrderService orderService;

    @GetMapping
    public Mono<String> cartItems(Model model) {
        return itemService.findAllInCartSorted()
                .collectList()
                .flatMap(items -> {
                    model.addAttribute("items", items);

                    BigDecimal totalPrice = items.stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    model.addAttribute("total", totalPrice);
                    model.addAttribute("empty", items.isEmpty());

                    return Mono.just("cart");
                });
    }

    @PostMapping("/{id}")
    public Mono<Rendering> cartChangeItem(@PathVariable(name = "id") Long id,
                                          ServerWebExchange exchange) {
        return exchange.getFormData()
                .flatMap(formData -> {
                    String action = formData.getFirst("action");
                    log.debug("cartChangeItem: id={}, action={}", id, action);

                    return itemService.changeCountItemsReactive(id, action)
                            .then(Mono.just(Rendering.redirectTo("/cart/items")
                                    .build()));
                });
    }

//    @PostMapping("/{id}")
//    public Mono<Rendering> changeItem(@PathVariable Long id, ServerWebExchange exchange) {
//        return exchange.getFormData()
//                .flatMap(formData -> {
//                    String action = formData.getFirst("action");
//                    log.debug("changeItem: id={}, action={}", id, action);
//
//                    return itemService.changeCountItemsReactive(id, action)
//                            .then(Mono.just(Rendering.redirectTo("/items/{id}")
//                                    .modelAttribute("id", id)
//                                    .build()));
//                });
//    }

//    @PostMapping("/buy")
//    public String cartBuyItems(RedirectAttributes redirectAttributes) {
//        log.debug("cartBuyItems: ");
//        Flux<Item> items = itemService.findAllInCartSorted();
//        Mono<Order> order = orderService.createOrder(items);
//        log.debug("cartBuyItems: order={}", order);
//        if (order != null) {
//            itemService.setItemCountZeroAllInCart();
//        }
//        redirectAttributes.addAttribute("id", order.getId());
//        return "redirect:/orders/{id}?newOrder=true";
//    }

    @PostMapping("/buy")
    public Mono<String> cartBuyItems() {
        return itemService.findAllInCartSorted()
                .collectList()
                .flatMap(items -> {
                    if (items.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Cart is empty"));
                    }
                    return orderService.createOrder(Flux.fromIterable(items));
                })
                .flatMap(order -> itemService.setItemCountZeroAllInCart()
                        .thenReturn("redirect:/orders/" + order.getId() + "?newOrder=true"))
                .onErrorResume(e -> Mono.just("redirect:/cart?error=" + e.getMessage()));
    }

}
