package com.example.community.dto;


import com.example.community.entity.Article;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequestDto {

    private Long id;
    // 댓글 내용 사이즈 1~280자
    @Size(min = 1,max = 280,message = "댓글은 1~280자 이내로 작성해주세요")
    private String content;
    // 멤버 Entity
    private Member member;
    // 글 Entity
    private Article article;
    // 글 id
    private Long boardid;
    // 부모 댓글 id
    private Long parentid;
    // 부모댓글
    private Comment parent;
    // 댓글 번호
    private Long commentnumber;
    // 댓글 번호
    private Long commentorder;
    // 댓글 깊이
    private int redepth;
    // 대댓글
    private List<Comment> child;
    // 대댓글이 있는 상태에서 부모 댓글 삭제 여부
    private Boolean deleted;

    // dto -> Entity 생성
    public Comment toEntity() {
        return Comment.builder().
                content(content).
                member(member).
                article(article).
                child(child).
                parent(parent).
                commentnumber(commentnumber).
                commentorder(commentorder).
                redepth(redepth).
                deleted(false)
                .build();
    }

    public void changeComment(Article article, Member member) {
        this.article = article;
        this.member = member;
        this.commentnumber = (long) article.getCommentcount();
        this.commentorder = 0L;
    }


    public void changeReplyEntity(Article article, Member member, Comment parentcomment) {
        this.article = article;
        this.member = member;
        this.parent = parentcomment;
    }

    public void changeCommentData(Long commentnumber, long commentorder, int i) {
        this.commentnumber = commentnumber;
        this.commentorder = commentorder;
        this.redepth = i;
    }
}
