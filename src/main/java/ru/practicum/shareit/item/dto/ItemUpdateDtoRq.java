package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.user.User;

@Data
@RequiredArgsConstructor
public class ItemUpdateDtoRq {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
}
