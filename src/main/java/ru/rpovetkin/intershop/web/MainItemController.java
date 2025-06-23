package ru.rpovetkin.intershop.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.ItemSort;
import ru.rpovetkin.intershop.model.Paging;
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
        log.debug("getItems: search={}, pageNumber={}, pageSize={}, sort={}", search, pageNumber, pageSize, ItemSort.valueOf(sort));

        Sort sorting = Sort.unsorted();
        if ("ALPHA".equals(sort)) {
            sorting = Sort.by("title").ascending();
        } else if ("PRICE".equals(sort)) {
            sorting = Sort.by("price").ascending();
        }

        Pageable pageable = PageRequest.of(
                pageNumber - 1,
                pageSize,
                sorting
        );

        List<Item> items = itemService.findAllWithPagination(pageable, search);
        model.addAttribute("items", items);

        Paging paging = new Paging(pageNumber, pageSize, false, false);
        model.addAttribute("paging", paging);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
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
