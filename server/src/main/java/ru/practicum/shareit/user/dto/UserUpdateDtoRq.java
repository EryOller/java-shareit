package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Setter
@Getter
@Builder
public class UserUpdateDtoRq {
    private String name;
    @Email
    private String email;
}
