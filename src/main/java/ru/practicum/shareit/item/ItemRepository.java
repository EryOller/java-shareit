package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item update(Item item);

    Item findById(int itemId);

    boolean isValidId(int id);

    List<Item> getListItemsByUserId(int userId);

    List<Item> getItemByText(String text);
}
