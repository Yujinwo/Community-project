package com.example.community.dto;


import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import com.example.community.entity.Reply;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReplyRequestDto {

    private Long id;

    @Size(min = 1,max = 280,message = "댓글은 1~280자 이내로 작성해주세요")
    private String content;

    private Comment comment;
    private Member member;
    private Long commentid;

    @Builder
    public ReplyRequestDto(Long id, String content, Comment comment, Member member){
        this.id = id;
        this.content = content;
        this.comment = comment;
        this.member = member;
    }
    public Reply toEntity(){
        return Reply.builder()
                .content(content)
                .member(member)
                .comment(comment)
                .build();
    }

}
