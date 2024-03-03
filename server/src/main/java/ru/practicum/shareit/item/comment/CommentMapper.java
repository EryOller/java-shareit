package ru.practicum.shareit.item.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.dto.CommentDtoRq;
import ru.practicum.shareit.item.comment.dto.CommentDtoRs;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

@Mapper(componentModel = org.mapstruct.MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {
    @Mapping(target = "authorName", source = "author.name")
    CommentDtoRs toCommentDtoRs(Comment comment);

    Comment toComment(CommentDtoRq commentDtoRq);

    List<CommentDtoRs> toListCommentDtoRs(List<Comment> comments);
}
