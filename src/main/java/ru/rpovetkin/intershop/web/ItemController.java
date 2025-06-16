package ru.rpovetkin.intershop.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.service.ItemService;

@Controller
@Slf4j
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping(value = "/{id}")
    public String changeItem(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "action") String action,
            RedirectAttributes redirectAttributes) {
        log.debug("changeItem: id={}, action={}", id, action);
        itemService.changeCountItems(id, action);
        redirectAttributes.addAttribute("id", id);
        return "redirect:/items/{id}";
    }

    @GetMapping("/{id}")
    public String showItems(@PathVariable(name = "id") Long id, Model model) {
        Item item = itemService.findById(id);
        model.addAttribute("item", item);
        return "item";
    }
}
