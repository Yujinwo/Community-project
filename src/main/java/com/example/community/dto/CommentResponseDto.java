package com.example.community.dto;


import com.example.community.entity.Article;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
// 상세 페이지 댓글 조회 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String content;
    private Member member;
    private Article article;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Comment parent;
    // 대댓글 컬렉션
    private List<Comment> child;
    // 대댓글이 있는 상태에서 댓글 삭제 여부
    private Boolean deleted;
    // 최상위 부모 댓글 번호
    private Long commentnumber;
    // 댓글 깊이
    private int redepth;

}
