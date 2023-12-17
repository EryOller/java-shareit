package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.item.comment.dto.CommentDtoRq;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ItemUpdateDtoRq {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    @ToString.Exclude
    private List<CommentDtoRq> comments;
}
