package ru.practicum.shareit.item.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findCommentsByItemId(int itemId);

    @Query("select c from Comment c " +
            "where c.item.owner.id = :userId ")
    List<Comment> findAllCommentsByItemOwnerId(@Param("userId") int userId);
}
