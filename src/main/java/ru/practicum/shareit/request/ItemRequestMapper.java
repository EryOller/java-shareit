package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDtoRs;
import ru.practicum.shareit.request.dto.ItemRequestDtoRq;
import ru.practicum.shareit.request.dto.ItemRequestDtoRs;
import ru.practicum.shareit.user.User;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {

    @Mapping(target = "id", source = "itemRequest.id")
    @Mapping(target = "items", expression = "java(setItems())")
    @Mapping(target = "created", source = "itemRequest.created")
    ItemRequestDtoRs toItemRequestDtoRs(ItemRequest itemRequest);

    @Mapping(target = "id", source = "request.id")
    @Mapping(target = "requester", source = "user")
    ItemRequest toItemRequest(ItemRequestDtoRq request, User user);

    default List<ItemDtoRs> setItems() {
        return Collections.emptyList();
    }
}