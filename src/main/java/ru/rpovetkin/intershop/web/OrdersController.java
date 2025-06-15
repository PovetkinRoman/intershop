package ru.rpovetkin.intershop.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.Order;
import ru.rpovetkin.intershop.model.OrderItem;
import ru.rpovetkin.intershop.service.OrderService;

import java.util.List;

@Controller
@RequestMapping("/orders")
@Slf4j
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    //и) GET "/orders" - список заказов
//		Возвращает:
//        		шаблон "orders.html"
//        		используется модель для заполнения шаблона:
//        			"orders" - List<Order> - список заказов
//        				"id" - идентификатор заказа
//        				"items" - List<Item> - список товаров в заказе (id, title, decription, imgPath, count, price)
    @GetMapping
    public String orders(Model model) {
        List<Order> orders = orderService.findAllOrders();
        model.addAttribute("orders", orders);
        return "orders";
    }

    //     к) GET "/orders/{id}" - карточка заказа
//       		Параматры:
//        		newOrder - true, если переход со страницы оформления заказа (по умолчанию, false)
//        	Возвращает:
//       			шаблон "order.html"
//       			используется модель для заполнения шаблона:
//       				"order" - заказ Order
//       					"id" - идентификатор заказа
//        				"items" - List<Item> - список товаров в заказе (id, title, decription, imgPath, count, price)
//        			"newOrder" - true, если переход со страницы оформления заказа (по умолчанию, false)
    @GetMapping("/{id}")
    public String order(@PathVariable(name = "id") Long id,
                        @RequestParam(name = "newOrder", required = false) Boolean newOrder,
                        Model model) {
        Order order = orderService.findOrderById(id);
        log.debug("order_items: {}", order.getOrderItems());
        model.addAttribute("order", order);
        model.addAttribute("id", order.getUuid());
        List<Item> items = order.getOrderItems().stream().map(OrderItem::getItem).toList();
        model.addAttribute("items", items);
        model.addAttribute("newOrder", newOrder);
        model.addAttribute("totalSum", order.getTotalSum());
        return "order";
    }
}
