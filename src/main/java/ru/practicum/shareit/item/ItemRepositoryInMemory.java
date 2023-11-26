package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryInMemory implements ItemRepository {
    private Map<Integer, Item> items = new HashMap<>();
    private int sequent = 0;

    @Override
    public Item save(Item item) {
        item.setId(getSequentForItem());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(int itemId) {
        return items.get(itemId);
    }

    @Override
    public boolean isValidId(int id) {
        return items.containsKey(id);
    }

    @Override
    public List<Item> getListItemsByUserId(int userId) {
        return items.values().stream()
                .filter(i -> i.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemByText(String text) {
        return items.values().stream()
                .filter(i -> isMatchItemWithText(i, text) && i.getAvailable())
                .collect(Collectors.toList());
    }

    private int getSequentForItem() {
        return ++sequent;
    }

    private boolean isMatchItemWithText(Item item, String text) {
        return item.getName().toLowerCase().contains(text.toLowerCase()) ||
                item.getDescription().toLowerCase().contains(text.toLowerCase());
    }
}
