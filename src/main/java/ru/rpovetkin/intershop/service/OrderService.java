//package ru.rpovetkin.intershop.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import ru.rpovetkin.intershop.model.Item;
//import ru.rpovetkin.intershop.model.Order;
//import ru.rpovetkin.intershop.repository.OrderRepository;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class OrderService {
//
//    public final OrderRepository orderRepository;
//
//    public Order createOrder(List<Item> items) {
//        Order order = new Order(items, Boolean.TRUE);
//        return orderRepository.save(order);
//    }
//
//    public Order findOrderById(Long id) {
//        return orderRepository.findById(id).orElseThrow();
//    }
//
//    public List<Order> findAllOrders() {
//        return orderRepository.findAll();
//    }
//}
