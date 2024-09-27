package com.example.community.dto;

import com.example.community.entity.Article;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
// 댓글 저장 dto
public class CommentSaveDto {
    private String content;
    private Member member;
    private Article article;
    // 부모댓글
    private Comment parent;
    // 최상위 부모 댓글 번호
    private Long commentnumber;

    // 부모 댓글 번호
    private Long commentorder;
    // 댓글 깊이
    private int redepth;
    public void changeCommentSaveData(String content,Article article, Member member) {
        this.content = content;
        this.article = article;
        this.member = member;
        this.commentnumber = (long) article.getCommentcount();
        this.commentorder = 0L;
    }
    public void changeReplySaveData(String content,Article article, Member member,Comment parent) {
        this.content = content;
        this.article = article;
        this.member = member;
        this.parent = parent;
    }
    public void changeReplySaveOrderData(Long commentnumber, Long commentorder, int redepth) {
        this.commentnumber = commentnumber;
        this.commentorder = commentorder;
        this.redepth = redepth;
    }
    public Comment createCommentEntity() {
        return Comment.builder().
                content(content).
                member(member).
                article(article).
                parent(parent).
                commentnumber(commentnumber).
                commentorder(commentorder).
                redepth(redepth).
                deleted(false)
                .build();
    }
}
