package com.example.community.dto;


import com.example.community.entity.Article;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

// 댓글 작성 요청 Dto
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    @NotEmpty(message = "데이터가 존재하지 않습니다.")
    @Size(min = 3,max = 280,message = "댓글은 3~280자 이내로 작성해주세요")
    private String content;
    private Long boardid;
    // 부모 댓글 id
    private Long parentid;
}
