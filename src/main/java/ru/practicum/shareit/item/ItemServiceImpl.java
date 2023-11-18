package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EditForbiddenException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDtoRs save(int ownerId, ItemSaveDtoRq itemDto) {
        if (userService.isValidId(ownerId)) {
            itemDto.setOwner(userService.findUserById(ownerId));
            return ItemMapper.INSTANCE.toItemDtoRs(itemRepository.save(ItemMapper.INSTANCE.toItem(itemDto)));
        } else {
            throw new IdNotFoundException("Владелец вещи с id " + ownerId + " не найден");
        }
    }

    @Override
    public ItemDtoRs update(int userId, int itemId, ItemUpdateDtoRq itemDto) {
        Item itemFromRepository = itemRepository.findById(itemId);
        if (itemFromRepository.getOwner().getId() == userId) {
            itemDto.setOwner(userService.findUserById(userId));
            Item item = ItemMapper.INSTANCE.toItem(itemDto);
            return updateItemByField(itemFromRepository, item);
        } else {
            throw new EditForbiddenException("Edit is forbidden for user with id " + userId);
        }
    }

    @Override
    public ItemDtoRs getItemById(int itemId) {
        if (itemRepository.isValidId(itemId)) {
            return ItemMapper.INSTANCE.toItemDtoRs(itemRepository.findById(itemId));
        } else {
            throw new IdNotFoundException("Вещь с id " + itemId + " не найдена");
        }
    }

    @Override
    public List<ItemDtoRs> getAllItemsByUserId(int userId) {
        return ItemMapper.INSTANCE.toListItemDtoRs(itemRepository.getListItemsByUserId(userId));
    }

    @Override
    public List<ItemDtoRs> searchItemByText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>(0);
        } else {
            return ItemMapper.INSTANCE.toListItemDtoRs(itemRepository.getItemByText(text));
        }
    }

    private ItemDtoRs updateItemByField(Item item, Item itemUpdate) {
        if (itemUpdate.getName() != null && !"".equals(itemUpdate.getName())) {
            item.setName(itemUpdate.getName());
        }
        if (itemUpdate.getDescription() != null && !"".equals(itemUpdate.getDescription())) {
            item.setDescription(itemUpdate.getDescription());
        }
        if (itemUpdate.getAvailable() != null) {
            item.setAvailable(itemUpdate.getAvailable());
        }
        return ItemMapper.INSTANCE.toItemDtoRs(item);
    }
}