package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ItemUpdateDtoRq {
    private int id;
    private String name;
    private String description;
    private Boolean available;
}
