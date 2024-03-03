package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDtoRq {
    private Integer id;
    @NotBlank()
    private String description;
    private User requester;
    private LocalDateTime created;
}