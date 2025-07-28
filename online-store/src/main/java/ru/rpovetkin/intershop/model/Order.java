package ru.rpovetkin.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.repository.ItemRepository;
import ru.rpovetkin.intershop.repository.OrderItemRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order {
    @Id
    private Long id;
    private Boolean isPaid = Boolean.FALSE;
    private UUID uuid = UUID.randomUUID();

    @Transient
    private BigDecimal totalSum;

    public Mono<BigDecimal> calculateTotalSum(OrderItemRepository orderItemRepo) {
        return orderItemRepo.findByOrderId(this.id)
                .map(oi -> oi.getPrice().multiply(BigDecimal.valueOf(oi.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Flux<Item> getItems(OrderItemRepository orderItemRepo, ItemRepository itemRepo, Long orderId) {
        return orderItemRepo.findByOrderId(orderId)
                .flatMap(orderItem -> itemRepo.findById(orderItem.getItemId()));
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", isPaid=" + isPaid +
                ", uuid=" + uuid +
                '}';
    }
}