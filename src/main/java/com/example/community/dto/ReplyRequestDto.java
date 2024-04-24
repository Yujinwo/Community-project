package com.example.community.dto;


import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import com.example.community.entity.Reply;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class ReplyRequestDto {

    private Long id;
    // 대댓글 내용 사이즈 1~280자
    @Size(min = 1,max = 280,message = "댓글은 1~280자 이내로 작성해주세요")
    private String content;
    // Comment Entity
    private Comment comment;
    // Member Entity
    private Member member;
    // Comment id
    private Long commentid;
    // dto -> Reply Entity 생성
    public Reply toEntity(){
        return Reply.builder()
                .content(content)
                .member(member)
                .comment(comment)
                .build();
    }

}
