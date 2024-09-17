package com.example.community.dto;


import com.example.community.entity.Article;
import com.example.community.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ArticleSaveDto {

    private String title;
    private String content;
    private Member member;

    public void changeSaveArticleData(String title,String content,Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }

    public Article createArticleEntity() {
        return Article.builder().title(title).content(content).member(member).build();
    }
}
