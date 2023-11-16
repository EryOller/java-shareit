package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

@Component
public class ItemMapper {
    private UserService userService;

    @Autowired
    public ItemMapper(UserService userService) {
        this.userService = userService;
    }

    public Item itemCreateDtoToItem(int ownerId, ItemSaveDtoRq itemDto) {
        return Item.builder()
                .available(itemDto.getAvailable())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(userService.findUserById(ownerId))
                .build();
    }

    public Item itemUpdateDtoToItem(int itemId, ItemUpdateDtoRq itemDto) {
        return Item.builder()
                .available(itemDto.getAvailable())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(userService.findUserById(itemId))
                .build();
    }

    public ItemDtoRs itemToItemDtoRs(Item item) {
        return ItemDtoRs.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();
    }
}
