package ru.rpovetkin.intershop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.Order;
import ru.rpovetkin.intershop.model.OrderItem;
import ru.rpovetkin.intershop.repository.OrderItemRepository;
import ru.rpovetkin.intershop.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    public final OrderRepository orderRepository;
    public final OrderItemRepository orderItemRepository;

    public Mono<Order> createOrder(Flux<Item> items) {
        Order order = new Order();
        order.setIsPaid(true);

        return orderRepository.save(order)
                .flatMap(savedOrder -> {
                    return items.collectList()
                            .flatMapMany(itemList -> {
                                List<OrderItem> orderItems = itemList.stream()
                                        .map(item -> new OrderItem(
                                                savedOrder.getId(),
                                                item.getId(),
                                                item.getPrice(),
                                                item.getCount()
                                        ))
                                        .collect(Collectors.toList());

                                return orderItemRepository.saveAll(orderItems);
                            })
                            .then(Mono.just(savedOrder));
                });
    }

    public Mono<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Flux<Order> findAllOrders() {
        return orderRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
