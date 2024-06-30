package com.example.community.dto;


import com.example.community.entity.Article;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    @NotNull
    private Long id;
    // 댓글 내용 사이즈 1~280자
    @NotNull
    @Size(min = 1,max = 280,message = "댓글은 1~280자 이내로 작성해주세요")
    private String content;
    // 멤버 Entity
    @NotNull
    private Member member;
    // 글 Entity
    @NotNull
    private Article article;
    // 작성 시간
    @NotNull
    private LocalDateTime createdDate;
    // 수정 시간
    @NotNull
    private LocalDateTime modifiedDate;
    // 부모 댓글
    private Comment parent;
    // 대댓글
    private List<Comment> child;
    // 대댓글이 있는 상태에서 부모 댓글 삭제 여부
    @NotNull
    private Boolean deleted;
    @NotNull
    // 댓글 번호
    private int commentnumber;
    @NotNull
    // 댓글 깊이
    private int redepth;

}
