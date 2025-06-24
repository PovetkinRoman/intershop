package ru.rpovetkin.intershop.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rpovetkin.intershop.model.Item;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {

//    @Query(value = "select * from item i " +
//            "where i.count > 0", nativeQuery = true)
//    List<Item> findItemsForCart();
//
//    @Transactional
//    @Modifying
//    @Query(value = "update item i set count = 0 where i.count > 0", nativeQuery = true)
//    int setItemCountNullAllInCart();

    @Query("SELECT * FROM item WHERE count > 0")
    Flux<Item> findItemsForCart();

    @Query("UPDATE item SET count = 0 WHERE count > 0")
    Mono<Integer> setItemCountZeroForAllInCart();

    Flux<Item> findAllBy(Pageable pageable);
}
