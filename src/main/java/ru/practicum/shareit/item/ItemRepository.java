package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer>, QuerydslPredicateExecutor<Item> {

    List<Item> getListItemsByOwnerIdOrderByIdAsc(int userId, Pageable pageable);

    @Query(value = "SELECT * FROM items as it " +
            "WHERE it.is_available = true " +
            "AND (it.name ILIKE %:text% " +
            "OR it.description ILIKE %:text%) ",
            nativeQuery = true)
    List<Item> getItemByText(@Param("text") String text, Pageable pageable);

    List<Item> findAllByRequestIdIn(List<Integer> requestIdList);

    List<Item> findAllByRequestId(Integer requestId);

    List<Item> findAllByOwnerId(Long userId, PageRequest pageRequest);
}
