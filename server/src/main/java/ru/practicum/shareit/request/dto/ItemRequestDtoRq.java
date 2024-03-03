package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDtoRq {
    private Integer id;
    private String description;
    private User requester;
    private LocalDateTime created;
}