package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDtoRq;
import ru.practicum.shareit.item.comment.dto.CommentDtoRs;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDtoRs save(int ownerId, ItemSaveDtoRq itemDto);

    ItemDtoRs update(int userId, int itemId, ItemUpdateDtoRq itemDto);

    ItemDtoRs getItemById(int userId, int itemId);

    List<ItemDtoRs> getAllItemWithPagination(int userId, int from, int size);


    List<ItemDtoRs> searchItemByTextWithPagination(String text, int from, int size);

    Item findItemById(int itemId);

    boolean isValid(int itemId);

    boolean isAvailableItem(int userId, int itemId);

    CommentDtoRs createComment(CommentDtoRq commentDtoLittle, int itemId, int userId);
}
