package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    Item toItem(ItemSaveDtoRq itemDto);

    Item toItem(ItemUpdateDtoRq itemDto);

    ItemDtoRs toItemDtoRs(Item item);

    List<ItemDtoRs> toListItemDtoRs(List<Item> items);
}
