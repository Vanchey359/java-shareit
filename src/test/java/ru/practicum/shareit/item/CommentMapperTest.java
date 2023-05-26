package ru.practicum.shareit.item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {
    private Comment comment;
    private CommentDto commentDto;
    private final CommentMapper commentMapper = new CommentMapper();
    @BeforeEach
    private void beforeEach() {
        comment = new Comment(1L, "Text", new Item(), new User(), null);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Text");
    }

    @Test
    public void toCommentDtoTest() {
        CommentDto dto = commentMapper.toDto(comment);

        assertEquals(dto.getId(), comment.getId());
        assertEquals(dto.getText(), comment.getText());
    }

    @Test
    public void toCommentModelTest() {
        Comment newComment = commentMapper.toComment(commentDto);

        assertEquals(newComment.getId(), commentDto.getId());
        assertEquals(newComment.getText(), commentDto.getText());
    }
}