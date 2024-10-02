package com.example.community.dto;


import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
// 상세 페이지 댓글 전체 조회 커스텀 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResultDto {
    private List<CommentResponseDto> content;
    private int totalPages;
    private Boolean hasResult;
    private Boolean last;
    private Boolean first;
    private Boolean previous;
    private Boolean next;
    private int number;
    private Long count;

    public static CommentResultDto createCommentResultDto(Page<CommentResponseDto> comment) {
        return CommentResultDto.builder().first(comment.isFirst()).last(comment.isLast()).hasResult(comment.hasContent()).previous(comment.hasPrevious()).next(comment.hasNext()).content(comment.getContent()).number(comment.getNumber()).totalPages(comment.getTotalPages()).count(comment.getTotalElements()).build();
    }
}
