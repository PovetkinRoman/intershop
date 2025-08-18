package ru.rpovetkin.intershop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.Order;
import ru.rpovetkin.intershop.model.OrderItem;
import ru.rpovetkin.intershop.repository.OrderItemRepository;
import ru.rpovetkin.intershop.repository.OrderRepository;
import ru.rpovetkin.intershop.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    public final OrderRepository orderRepository;
    public final OrderItemRepository orderItemRepository;
    public final UserRepository userRepository;

    public Mono<Order> createOrder(Flux<Item> items, String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User not found")))
                .flatMap(user -> {
                    Order order = new Order();
                    order.setIsPaid(true);
                    order.setUserId(user.getId());
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
                });
    }

    public Mono<Order> findOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Flux<Order> findAllOrdersByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMapMany(user -> orderRepository.findAllByUserId(user.getId()));
    }

    public Mono<Order> findOrderByIdForUser(Long id, String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> orderRepository.findById(id)
                        .filter(order -> order.getUserId() != null && order.getUserId().equals(user.getId())));
    }
}
