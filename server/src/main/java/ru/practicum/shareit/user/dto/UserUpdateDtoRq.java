package ru.practicum.shareit.user.dto;

import lombok.*;


@Setter
@Getter
@Builder
public class UserUpdateDtoRq {
    private String name;
    private String email;
}
