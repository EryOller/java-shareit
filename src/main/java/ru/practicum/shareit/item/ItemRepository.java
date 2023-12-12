package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer>, QuerydslPredicateExecutor<Item> {

    List<Item> getListItemsByOwnerIdOrderByIdAsc(int userId);

    @Query(value = "SELECT * FROM items as it " +
            "WHERE it.is_available = true " +
            "AND (it.name ILIKE concat('%', :text,'%') " +
            "OR it.description ILIKE concat('%', :text,'%')); ", nativeQuery = true)
    List<Item> getItemByText(@Param("text") String text);
}
