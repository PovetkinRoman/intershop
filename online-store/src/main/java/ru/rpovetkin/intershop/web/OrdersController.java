package ru.rpovetkin.intershop.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.OrderItemDto;
import ru.rpovetkin.intershop.repository.OrderItemRepository;
import ru.rpovetkin.intershop.service.ItemService;
import ru.rpovetkin.intershop.service.OrderService;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersController {

    private final OrderService orderService;
    private final ItemService itemService;
    private final OrderItemRepository orderItemRepo;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Mono<String> orders(Model model, org.springframework.web.server.ServerWebExchange exchange) {
        return exchange.getPrincipal().map(java.security.Principal::getName)
                .flatMapMany(orderService::findAllOrdersByUsername)
                .flatMap(order ->
                        orderItemRepo.findByOrderId(order.getId())
                                .flatMap(orderItem ->
                                        itemService.findById(orderItem.getItemId())
                                                .map(item -> Map.of(
                                                        "title", item.getTitle(),
                                                        "count", orderItem.getCount(),
                                                        "price", orderItem.getPrice(),
                                                        "total", orderItem.getPrice().multiply(new BigDecimal(orderItem.getCount()))
                                                ))
                                )
                                .collectList()
                                .map(items -> {
                                    BigDecimal totalSum = items.stream()
                                            .map(item -> (BigDecimal) item.get("total"))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                                    return Map.of(
                                            "id", order.getId(),
                                            "uuid", order.getUuid(),
                                            "items", items,
                                            "totalSum", totalSum
                                    );
                                })
                )
                .collectList()
                .doOnNext(orders -> model.addAttribute("orders", orders))
                .thenReturn("orders");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Mono<String> order(
            @PathVariable Long id,
            @RequestParam(name = "newOrder", required = false) Boolean newOrder,
            Model model
    ) {
        return ReactiveSecurityContextHolder.getContext()
                .map(sc -> sc.getAuthentication().getName())
                .flatMap(username -> orderService.findOrderByIdForUser(id, username))
                .flatMap(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("id", order.getUuid());
                    model.addAttribute("newOrder", newOrder);

                    return orderItemRepo.findByOrderId(id)
                            .flatMap(orderItem -> itemService.findById(orderItem.getItemId())
                                    .map(item -> new OrderItemDto(item, orderItem.getPrice(), orderItem.getCount()))
                            )
                            .collectList()
                            .doOnNext(items -> {
                                log.debug("items from order: {}", items);
                                model.addAttribute("items", items);
                            })
                            .then(order.calculateTotalSum(orderItemRepo))
                            .doOnNext(totalSum -> {
                                model.addAttribute("totalSum", totalSum);
                            });
                })
                .thenReturn("order");
    }
}
