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

    @GetMapping
    public String cartItems(Model model) {
        List<Item> items = itemService.findAllInCart();
        model.addAttribute("items", items);
        BigDecimal totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("total", totalPrice);
        model.addAttribute("empty", items.isEmpty() ? Boolean.TRUE : Boolean.FALSE);
        return "cart";
    }

    @PostMapping("/{id}")
    public String cartChangeItem(@PathVariable(name = "id") Long id,
                                 @RequestParam String action) {
        log.debug("cartChangeItem: id={}, action={}", id, action);
        itemService.changeCountItems(id, action);
        return "redirect:/cart/items";
    }

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
