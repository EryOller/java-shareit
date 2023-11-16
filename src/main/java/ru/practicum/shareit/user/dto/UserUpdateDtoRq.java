package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

@AllArgsConstructor
@Builder
@Data
public class UserUpdateDtoRq {
    private String name;
    @Email
    private String email;
}
