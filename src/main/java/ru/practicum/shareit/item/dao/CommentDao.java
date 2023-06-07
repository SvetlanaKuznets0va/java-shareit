package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentDao extends JpaRepository<Comment, Integer> {
    List<Comment> findCommentsByItemId(int itemId);
}
