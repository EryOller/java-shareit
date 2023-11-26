package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class UserCreateDtoRq {
    private String name;
    @Email
    @NotEmpty
    private String email;
}
