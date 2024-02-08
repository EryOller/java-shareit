package ru.practicum.shareit.user.dto;

import lombok.*;


@Setter
@Getter
@Builder
public class UserCreateDtoRq {
    private String name;
    private String email;
}
