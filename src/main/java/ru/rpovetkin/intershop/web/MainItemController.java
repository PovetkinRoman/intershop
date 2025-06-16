package ru.rpovetkin.intershop.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.Paging;
import ru.rpovetkin.intershop.model.Sort;
import ru.rpovetkin.intershop.service.ItemService;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/main/items")
public class MainItemController {

    private final ItemService itemService;

    public MainItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String getItems(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "sort", defaultValue = "NO") String sort,
            @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, Model model) {
        log.debug("getItems: search={}, pageNumber={}, pageSize={}, sort={}", search, pageNumber, pageSize, Sort.valueOf(sort));
        //TODO: добавить пагинацию
        List<Item> items = itemService.findAll();
        model.addAttribute("items", items);

        Paging paging = new Paging(pageNumber, pageSize, false, false);
        model.addAttribute("paging", paging);
        model.addAttribute("search", search);
        return "main";
    }

    @PostMapping(value = "/{id}")
    public String changeItem(
            @PathVariable(name = "id") Long id,
            @RequestParam(name = "action") String action) {
        log.debug("changeItem: id={}, action={}", id, action);
        itemService.changeCountItems(id, action);
        return "redirect:/main/items";
    }

    @GetMapping("/{id}")
    public String showItems(@PathVariable(name = "id") Long id, Model model) {
        Item item = itemService.findById(id);
        model.addAttribute("item", item);
        return "item";
    }

}
