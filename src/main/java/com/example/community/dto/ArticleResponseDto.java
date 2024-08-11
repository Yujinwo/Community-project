package com.example.community.dto;

import com.example.community.entity.*;
import jakarta.persistence.Column;
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
public class ArticleResponseDto {

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
    // 이미지
    private List<String> imageUrls;
    // 태그
    private List<String> tagConents;
    // 댓글
    private List<Comment> comments;
    // 댓글 수
    @NotNull
    private int commentcount;

    @Builder
    public ArticleResponseDto(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdDate = article.getCreatedDate();
        this.modifiedDate = article.getModifiedDate();
        this.member = article.getMember();
        //멤버 프록시 강제 초기화
        member.getUserpw();
        this.viewcount = article.getViewcount();
        // 이미지 파일이 Null 아닐 시 url 리스트 생성
        List<BoardImage> boardImages = article.getBoardImages();
        if (boardImages.size() != 0) {
            this.imageUrls = boardImages.stream()
                    .map(BoardImage::getUrl)
                    .collect(Collectors.toList());
        } else {
            this.imageUrls = Collections.emptyList();
        }
        List<Tag> tags = article.getTags();
        if(tags.size() != 0) {
            this.tagConents = tags.stream().map(tag -> tag.getContent()).collect(Collectors.toList());
        }
        else {
            this.tagConents = Collections.emptyList();
        }
        this.comments = article.getComments();
        this.commentcount = article.getCommentcount();
    }

    public ArticleResponseDto toDto(Article article) {

        return ArticleResponseDto.builder()
                .article(article)
                .build();
    }


}
