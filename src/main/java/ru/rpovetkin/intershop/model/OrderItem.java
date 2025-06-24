//package ru.rpovetkin.intershop.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "order_item")
//public class OrderItem {
//    @EmbeddedId
//    private OrderItemId id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("orderId")
//    @JoinColumn(name = "order_id")
//    private Order order;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("itemId")
//    @JoinColumn(name = "item_id")
//    private Item item;
//
//    @Column(nullable = false)
//    private BigDecimal quantity;
//
//    @Column(nullable = false)
//    private Integer count;
//
//    public OrderItem(Order order, Item item, BigDecimal quantity, Integer count) {
//        this.order = order;
//        this.item = item;
//        this.quantity = quantity;
//        this.id = new OrderItemId(order.getId(), item.getId());
//        this.count = count;
//    }
//}
