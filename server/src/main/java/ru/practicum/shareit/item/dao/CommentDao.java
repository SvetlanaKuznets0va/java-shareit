package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentDao extends JpaRepository<Comment, Integer> {
    @Query("select c from Comment as c where c.itemId in ?1")
    List<Comment> findCommentsByItemsId(List<Integer> itemsId);
}
