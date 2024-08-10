package com.example.community.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResultDto {
    private List<CommentResponseDto> content;
    private int totalPages;
    private Boolean hasResult;
    private Boolean last;
    private Boolean first;
    private Boolean previous;
    private Boolean next;
    private int number;

    public static CommentResultDto createCommentResultDto(Page<CommentResponseDto> comment) {
        return CommentResultDto.builder().first(comment.isFirst()).last(comment.isLast()).hasResult(comment.hasContent()).previous(comment.hasPrevious()).next(comment.hasNext()).content(comment.getContent()).number(comment.getNumber()).totalPages(comment.getTotalPages()).build();
    }
}
