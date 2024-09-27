package com.example.community.dto;

import com.example.community.entity.Article;
import com.example.community.entity.Member;
import com.example.community.entity.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


// Home 페이지 글 전체 조회 DTO
@Getter
@NoArgsConstructor
public class ArticleindexResponseDto {


    @NotNull
    private Long id;
    @NotNull
    @Size(min=1,max=60,message = "제목은 1~60자 이내로 작성해주세요")
    private String title;
    @NotNull
    @Size(min=1,max=1000,message = "내용은 1~1000자 이내로 작성해주세요")
    private String content;
    @NotNull
    private Member member;
    @NotNull
    private LocalDateTime createdDate;
    @NotNull
    private LocalDateTime modifiedDate;
    @NotNull
    private int viewcount;
    // 태그
    private List<String> tagConents;


    @Builder
    public ArticleindexResponseDto(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdDate = article.getCreatedDate();
        this.modifiedDate = article.getModifiedDate();
        this.member = article.getMember();
        //멤버 프록시 강제 초기화
        member.getUserpw();
        this.viewcount = article.getViewcount();
        List<Tag> tags = article.getTags();
        // 태그 컬렉션을 String형식 List로 변환
        if(!tags.isEmpty()) {
            this.tagConents = tags.stream().map(tag -> tag.getContent()).collect(Collectors.toList());
        }
        else {
            this.tagConents = Collections.emptyList();
        }
    }
    public ArticleindexResponseDto toDto(Article article) {
        return ArticleindexResponseDto.builder()
                .article(article)
                .build();
    }


}
