package ru.rpovetkin.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("order_item")
public class OrderItem {
    @Id
    private Long id;
    private Long orderId;
    private Long itemId;
    private BigDecimal price;
    private Integer count;

    public OrderItem(Long orderId, Long itemId, BigDecimal price, Integer count) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.price = price;
        this.count = count;
    }
}