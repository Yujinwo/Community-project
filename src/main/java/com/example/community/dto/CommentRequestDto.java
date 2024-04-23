package com.example.community.dto;


import com.example.community.entity.Article;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import com.example.community.entity.Reply;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequestDto {

    private Long id;

    @Size(min = 1,max = 280,message = "댓글은 1~280자 이내로 작성해주세요")
    private String content;
    private Member member;
    private Article article;
    private Long boardid;
    private List<Reply> child;
    private Boolean deleted = false;

    @Builder
    public CommentRequestDto(Long id, String content, Member member, Article article, List<Reply> child, boolean deleted){
        this.id = id;
        this.content = content;
        this.member = member;
        this.article = article;
        this.child = child;
        this.deleted = deleted;
    }
    public Comment toEntity() {
        return Comment.builder().content(content).member(member).article(article).child(child).deleted(deleted).build();
    }

}
