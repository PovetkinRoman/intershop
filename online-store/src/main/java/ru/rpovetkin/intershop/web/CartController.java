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
import ru.rpovetkin.intershop.service.ItemService;
import ru.rpovetkin.intershop.service.OrderService;
import org.springframework.web.reactive.function.client.WebClient;
import java.math.BigDecimal;
import java.time.Duration;

@Controller
@RequestMapping("/cart/items")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final ItemService itemService;
    private final OrderService orderService;
    private final WebClient webClient = WebClient.create("http://payment-service-app:8080/payment");

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

                    if (items.isEmpty()) {
                        model.addAttribute("canBuy", false);
                        return Mono.just("cart");
                    }

                    // Запрос к payment-service
                    return webClient.get()
                            .uri(uriBuilder -> uriBuilder.queryParam("amountForPay", totalPrice).build())
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .timeout(Duration.ofSeconds(2))
                            .map(canBuy -> {
                                model.addAttribute("canBuy", canBuy);
                                return "cart";
                            })
                            .onErrorResume(ex -> {
                                model.addAttribute("canBuy", false);
                                model.addAttribute("paymentError", "Платежный сервис временно недоступен. Оформление заказа невозможно.");
                                return Mono.just("cart");
                            });
                });
    }

    @PostMapping("/{id}")
    public Mono<String> cartChangeItem(@PathVariable(name = "id") Long id, ServerWebExchange exchange) {
        return exchange.getFormData()
            .flatMap(formData -> {
                String action = formData.getFirst("action");
                return itemService.changeCountItemsReactive(id, action)
                    .thenReturn("redirect:/cart/items");
            });
    }

    @PostMapping("/buy")
    public Mono<String> cartBuyItems() {
        return itemService.findAllInCartSorted()
                .collectList()
                .flatMap(items -> {
                    if (items.isEmpty()) {
                        return Mono.error(new IllegalArgumentException("Cart is empty"));
                    }
                    BigDecimal totalPrice = items.stream()
                            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    // Запрос на оплату
                    return webClient.post()
                            .uri(uriBuilder -> uriBuilder.queryParam("amountForPay", totalPrice).build())
                            .retrieve()
                            .bodyToMono(Boolean.class)
                            .onErrorReturn(false)
                            .flatMap(isPayed -> {
                                if (!isPayed) {
                                    return Mono.just("redirect:/cart/items?error=Недостаточно средств для оплаты");
                                }
                                return orderService.createOrder(Flux.fromIterable(items))
                                        .flatMap(order -> itemService.setItemCountZeroAllInCart()
                                                .thenReturn("redirect:/orders/" + order.getId() + "?newOrder=true"));
                            });
                })
                .onErrorResume(e -> Mono.just("redirect:/cart?error=" + e.getMessage()));
    }

}
