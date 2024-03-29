package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CommentDtoRq {
    @NotBlank
    private String text;
    private Integer itemId;
    private Integer authorId;
}
