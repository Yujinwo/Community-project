package com.example.community.dto;


import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ReplyResponseDto {
    @NotNull
    private Long id;
    // 대댓글 내용 사이즈 1~280자
    @NotNull
    @Size(min = 1,max = 280,message = "댓글은 1~280자 이내로 작성해주세요")
    private String content;
    // Comment Entity
    @NotNull
    private Comment comment;
    // Member Entity
    @NotNull
    private Member member;
    // 작성 시간
    @NotNull
    private LocalDateTime createdDate;
    // 수정 시간
    @NotNull
    private LocalDateTime modifiedDate;

}
