package ru.rpovetkin.intershop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rpovetkin.intershop.model.Item;
import ru.rpovetkin.intershop.model.ItemCardDto;
import ru.rpovetkin.intershop.model.ItemListDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemMapperService {

    /**
     * Преобразует Item в ItemCardDto
     */
    public ItemCardDto toItemCardDto(Item item) {
        log.debug("Mapping item to ItemCardDto for id: {}", item.getId());
        return new ItemCardDto(
                item.getId(),
                item.getImgPath(),
                item.getTitle(),
                item.getPrice(),
                item.getDescription()
        );
    }

    /**
     * Преобразует Item в ItemListDto
     */
    public ItemListDto toItemListDto(Item item) {
        log.debug("Mapping item to ItemListDto for id: {}", item.getId());
        return new ItemListDto(
                item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice()
        );
    }

    /**
     * Преобразует список Item в список ItemListDto
     */
    public List<ItemListDto> toItemListDtoList(List<Item> items) {
        log.debug("Mapping {} items to ItemListDto list", items.size());
        return items.stream()
                .map(this::toItemListDto)
                .collect(Collectors.toList());
    }
} 