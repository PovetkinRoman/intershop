package ru.rpovetkin.intershop.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.Order;
import ru.rpovetkin.intershop.service.ItemService;
import ru.rpovetkin.intershop.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cart/items")
@Slf4j
@RequiredArgsConstructor
public class CartController {

    private final ItemService itemService;
    private final OrderService orderService;

    //г) GET "/cart/items" - список товаров в корзине
//        	Возвращает:
//        		шаблон "cart.html"
//        		используется модель для заполнения шаблона:
//        			"items" - List<Item> - список товаров в корзине (id, title, decription, imgPath, count, price)
//        			"total" - суммарная стоимость заказа
//        			"empty" - true, если в корзину не добавлен ни один товар
    @GetMapping
    public String cartItems(Model model) {
        List<Item> items = itemService.findAllInCart();
        model.addAttribute("items", items);
        BigDecimal totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("total", totalPrice);
        model.addAttribute("empty", items.isEmpty() ? Boolean.TRUE : Boolean.FALSE);
//        log.debug("cartItems: id={}, action={}", id, action);
        return "cart";
    }

    //    д) POST "/cart/items/{id}" - изменить количество товара в корзине
//       		Параматры:
//        		action - значение из перечисления PLUS|MINUS|DELETE (PLUS - добавить один товар, MINUS - удалить один товар, DELETE - удалить товар из корзины)
//        	Возвращает:
//        		редирект на "/cart/items"
    @PostMapping("/{id}")
    public String cartChangeItem(@PathVariable(name = "id") Long id,
                                 @RequestParam String action) {
        log.debug("cartChangeItem: id={}, action={}", id, action);
        itemService.changeCountItems(id, action);
        return "redirect:/cart/items";
    }

//    	з) POST "/buy" - купить товары в корзине (выполняет покупку товаров в корзине и очищает ее)
//		Возвращает:
//			редирект на "/orders/{id}?newOrder=true"

    //TODO: доделать логику удаления товаров
    @PostMapping("/buy")
    public String cartBuyItems(RedirectAttributes redirectAttributes) {
        log.debug("cartBuyItems: ");
        List<Item> items = itemService.findAllInCart();
        Order order = orderService.createOrder(items);
        log.debug("cartBuyItems: order={}", order);
        if (order != null) {
            itemService.setItemCountNullAllInCart();
        }
        redirectAttributes.addAttribute("id", order.getId());
        return "redirect:/orders/{id}?newOrder=true";
    }
}
