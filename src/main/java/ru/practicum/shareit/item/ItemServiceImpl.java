package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EditForbiddenException;
import ru.practicum.shareit.exception.IdNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.item.dto.ItemSaveDtoRq;
import ru.practicum.shareit.item.dto.ItemUpdateDtoRq;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserService userService;
    @Autowired
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemDtoRs save(int ownerId, ItemSaveDtoRq itemDto) {
        if (userService.isValidId(ownerId)) {
            itemDto.setOwner(userService.findUserById(ownerId));
            return itemMapper.toItemDtoRs(itemRepository.save(itemMapper.toItem(itemDto)));
        } else {
            throw new IdNotFoundException("Владелец вещи с id " + ownerId + " не найден");
        }
    }

    @Transactional
    @Override
    public ItemDtoRs update(int userId, int itemId, ItemUpdateDtoRq itemDto) {
        Item itemFromRepository = itemRepository.findById(itemId).get();
        if (itemFromRepository.getOwner().getId() == userId) {
            itemDto.setOwner(userService.findUserById(userId));
            Item item = itemMapper.toItem(itemDto);
            return updateItemByField(itemFromRepository, item);
        } else {
            throw new EditForbiddenException("Edit is forbidden for user with id " + userId);
        }
    }

    @Override
    public ItemDtoRs getItemById(int itemId) {
        if (itemRepository.existsById(itemId)) {
            return itemMapper.toItemDtoRs(itemRepository.findById(itemId).get());
        } else {
            throw new IdNotFoundException("Вещь с id " + itemId + " не найдена");
        }
    }

    @Override
    public List<ItemDtoRs> getAllItemsByUserId(int userId) {
        return itemMapper.toListItemDtoRs(itemRepository.getListItemsByOwnerId(userId));
    }

    @Override
    public List<ItemDtoRs> searchItemByText(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>(0);
        } else {
            return itemMapper.toListItemDtoRs(itemRepository.getItemByText(text));
        }
    }

    @Transactional
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
        return itemMapper.toItemDtoRs(item);
    }
}