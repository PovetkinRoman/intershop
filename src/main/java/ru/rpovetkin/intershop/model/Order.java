package ru.rpovetkin.intershop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean isPaid = Boolean.FALSE;
    @Column(unique = true, nullable = false, columnDefinition = "UUID")
    private UUID uuid = UUID.randomUUID();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public Order(List<Item> items, Boolean isPaid) {
        addItems(items);
        this.isPaid = isPaid;
    }

    public void addItems(List<Item> items) {
        for (Item item : items) {
            OrderItem orderItem =
                    new OrderItem(this,
                            item,
                            item.getPrice(),
                            item.getCount());
            orderItems.add(orderItem);
        }
    }


    public BigDecimal getTotalSum() {
        return orderItems.stream()
                .map(oi -> oi.getQuantity().multiply(BigDecimal.valueOf(oi.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Item> getItems() {
        return orderItems.stream().map(OrderItem::getItem).toList();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", isPaid=" + isPaid +
                ", uuid=" + uuid +
                ", orderItems=" + orderItems.size() +
                '}';
    }
}
