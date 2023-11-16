package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;

import java.util.List;

public interface ItemService {
    ItemDtoRs save(int ownerId, ItemSaveDtoRq itemDto);

    ItemDtoRs update(int userId, int itemId, ItemUpdateDtoRq itemDto);

    ItemDtoRs getItemById(int itemId);

    List<ItemDtoRs> getAllItemsByUserId(int userId);

    List<ItemDtoRs> searchItemByText(String text);
}
