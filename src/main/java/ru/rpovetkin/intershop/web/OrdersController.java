package ru.rpovetkin.intershop.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.rpovetkin.intershop.service.ItemService;

@Controller
@RequestMapping("/orders")
@Slf4j
public class OrdersController {

    private final ItemService itemService;

    public OrdersController(ItemService itemService) {
        this.itemService = itemService;
    }

    //и) GET "/orders" - список заказов
//		Возвращает:
//        		шаблон "orders.html"
//        		используется модель для заполнения шаблона:
//        			"orders" - List<Order> - список заказов
//        				"id" - идентификатор заказа
//        				"items" - List<Item> - список товаров в заказе (id, title, decription, imgPath, count, price)
    @GetMapping
    public String orders(Model model) {
        //TODO: вытаскивать все ордерс
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
    public String order(@PathVariable(name = "id") Long id, Model model) {
        //TODO: вытаскивать ордер
        return "order";
    }
}
