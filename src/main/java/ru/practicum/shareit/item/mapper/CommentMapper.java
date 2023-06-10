package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment, String authorName) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated()
        );
    }

    public static Comment toComment(Booking booking, CommentDto text) {
        return new Comment(
                0,
                text.getText(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                LocalDateTime.now()
        );
    }
}
