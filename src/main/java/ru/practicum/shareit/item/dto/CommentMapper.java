package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

@Component
public class CommentMapper {
    public Comment toComment(CommentDto commentDto) {
        Comment result = new Comment();
        result.setId(commentDto.getId());
        result.setText(commentDto.getText());
        result.setCreated(commentDto.getCreated());
        return result;
    }

    public CommentDto toDto(Comment comment) {
        CommentDto result = new CommentDto();
        result.setId(comment.getId());
        result.setText(comment.getText());
        result.setAuthorName(comment.getAuthor().getName());
        result.setCreated(comment.getCreated());
        return result;
    }
}