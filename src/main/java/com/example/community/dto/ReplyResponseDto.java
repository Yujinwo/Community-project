package com.example.community.dto;


import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import com.example.community.entity.Reply;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReplyResponseDto {

    private Long id;

    @Size(min = 1,max = 280,message = "댓글은 1~280자 이내로 작성해주세요")
    private String content;

    private Comment comment;
    private Member member;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    public ReplyResponseDto(Long id, String content, Comment comment, Member member, LocalDateTime createdDate, LocalDateTime modifiedDate){
        this.id = id;
        this.content = content;
        this.comment = comment;
        this.member = member;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }


}
