package ru.rpovetkin.intershop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("cart_item")
public class CartItem {
    @Id
    private Long id;
    private Long userId;
    private Long itemId;
    private Integer count;

    public CartItem(Long userId, Long itemId, Integer count) {
        this.userId = userId;
        this.itemId = itemId;
        this.count = count;
    }
}


