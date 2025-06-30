package ru.rpovetkin.intershop.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.rpovetkin.intershop.model.Order;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long> {

    @Query("SELECT * FROM orders WHERE is_paid = :isPaid")
    Flux<Order> findByIsPaid(boolean isPaid);
}
