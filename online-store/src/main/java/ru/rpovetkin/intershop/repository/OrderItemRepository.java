package ru.rpovetkin.intershop.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.OrderItem;

@Repository
public interface OrderItemRepository extends R2dbcRepository<OrderItem, Long> {

    @Query("SELECT * FROM order_item WHERE order_id = :orderId")
    Flux<OrderItem> findByOrderId(Long orderId);

    @Query("DELETE FROM order_item WHERE order_id = :orderId")
    Mono<Void> deleteByOrderId(Long orderId);
}
