package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class ItemDtoRs {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
}
