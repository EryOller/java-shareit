package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserDtoRs;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

import java.util.List;

public interface UserService {
    UserDtoRs save(UserCreateDtoRq userDto);

    UserDtoRs findById(int userId);

    User findUserById(int userId);

    List<UserDtoRs> getUsers();

    UserDtoRs updateUserById(int userid, UserUpdateDtoRq userDto);

    void deleteUserById(int userId);

    boolean isValidId(int id);
}
