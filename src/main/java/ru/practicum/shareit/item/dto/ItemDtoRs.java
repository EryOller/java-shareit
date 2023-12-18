package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.item.comment.dto.CommentDtoRs;

import java.util.List;

@Data
@Builder
public class ItemDtoRs {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemDtoRs lastBooking;
    private BookingItemDtoRs nextBooking;
    @ToString.Exclude
    private List<CommentDtoRs> comments;
}
