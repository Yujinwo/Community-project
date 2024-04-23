package com.example.community.dto;


import com.example.community.entity.Article;
import com.example.community.entity.Member;
import com.example.community.entity.Reply;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponseDto {

    private Long id;

    @Size(min = 1,max = 280,message = "댓글은 1~280자 이내로 작성해주세요")
    private String content;
    private Member member;
    private Article article;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private List<Reply> child;
    private Boolean deleted;

    @Builder
    public CommentResponseDto(Long id, String content, Member member, Article article, LocalDateTime createdDate, LocalDateTime modifiedDate, List<Reply> child, boolean deleted){
        this.id = id;
        this.content = content;
        this.member = member;
        this.article = article;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.child = child;
        this.deleted = deleted;
    }


}
