package ru.practicum.shareit.user;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserDtoRs;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

@Component
public class UserMapper {
    public User userUpdateDtoToUser(UserUpdateDtoRq userDto) {
        return User.builder().email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public User userCreateDtoToUser(UserCreateDtoRq userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public UserDtoRs userToUserDtoRs(User user) {
        return UserDtoRs.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
