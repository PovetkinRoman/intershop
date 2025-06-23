package ru.rpovetkin.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.rpovetkin.intershop.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select * from item i " +
            "where i.count > 0", nativeQuery = true)
    List<Item> findItemsForCart();

    @Transactional
    @Modifying
    @Query(value = "update item i set count = 0 where i.count > 0", nativeQuery = true)
    int setItemCountNullAllInCart();
}
