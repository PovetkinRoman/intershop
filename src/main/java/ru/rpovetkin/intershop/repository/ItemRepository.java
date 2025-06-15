package ru.rpovetkin.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.rpovetkin.intershop.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select * from item i " +
            "where i.count > 0", nativeQuery = true)
    List<Item> findItemsForCart();
}
