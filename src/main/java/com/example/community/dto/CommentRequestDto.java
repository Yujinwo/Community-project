package com.example.community.dto;


import com.example.community.entity.Article;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

// 댓글 작성 요청 Dto
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

    @Size(min = 1,max = 280,message = "댓글은 1~280자 이내로 작성해주세요")
    private String content;
    private Long boardid;
    // 부모 댓글 id
    private Long parentid;
}
