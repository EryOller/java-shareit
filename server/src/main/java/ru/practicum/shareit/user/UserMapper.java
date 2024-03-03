package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateDtoRq;
import ru.practicum.shareit.user.dto.UserDtoRs;
import ru.practicum.shareit.user.dto.UserUpdateDtoRq;

import java.util.List;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    User toUser(UserUpdateDtoRq userDto);

    User toUser(UserCreateDtoRq userDto);

    UserDtoRs toUserDtoRs(User user);

    List<UserDtoRs> toListItemDtoRs(List<User> users);
}
