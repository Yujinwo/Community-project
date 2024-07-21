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

@Data
@NoArgsConstructor
@Slf4j
public class ArticleindexResponseDto {

    @NotNull
    private Long id;
    // 제목 사이즈 1~60자
    @NotNull
    @Size(min=1,max=60,message = "제목은 1~60자 이내로 작성해주세요")
    private String title;
    // 제목 사이즈 1~1000자
    @NotNull
    @Size(min=1,max=1000,message = "내용은 1~1000자 이내로 작성해주세요")
    private String content;
    // 멤버 Entity
    @NotNull
    private Member member;
    // 작성 시간
    @NotNull
    private LocalDateTime createdDate;
    // 수정 시간
    @NotNull
    private LocalDateTime modifiedDate;
    // 조회수
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
