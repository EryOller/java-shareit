package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.item.comment.dto.CommentDtoRq;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class ItemSaveDtoRq {
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    @ToString.Exclude
    private List<CommentDtoRq> comments;
    private Integer requestId;
}
