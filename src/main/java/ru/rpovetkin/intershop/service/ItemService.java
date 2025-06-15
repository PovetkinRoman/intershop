package ru.rpovetkin.intershop.service;

import org.springframework.stereotype.Service;
import ru.rpovetkin.intershop.model.Action;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.repository.ItemRepository;

import java.util.Comparator;
import java.util.List;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void changeCountItems(Long id, String action) {
        Item item = itemRepository.findById(id).orElseThrow();
        switch (Action.valueOf(action.trim().toUpperCase())) {
            case DELETE:
                item.setCount(0);
                break;
            case PLUS:
                item.setCount(item.getCount() + 1);
                break;
            case MINUS:
                item.setCount(Math.max(item.getCount() - 1, 0));
                break;
            default: throw new IllegalStateException("Unexpected value: " + action);
        }
        itemRepository.save(item);
    }

    public List<Item> findAll() {
        return itemRepository.findAll().stream().sorted(Comparator.comparing(Item::getId)).toList();
    }

    public List<Item> findAllInCart() {
        return itemRepository.findItemsForCart().stream().sorted(Comparator.comparing(Item::getId)).toList();
    }

    public void setItemCountNullAllInCart() {
        itemRepository.setItemCountNullAllInCart();
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow();
    }
}
