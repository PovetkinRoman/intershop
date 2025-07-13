package ru.rpovetkin.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItemDto {
    private Item item;
    private BigDecimal price;
    private Integer count;

    public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(count));
    }
}
