package com.example.community.dto;

import com.example.community.entity.BoardImage;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
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
    // 댓글
    private List<Comment> comments;
    // 댓글 수
    @NotNull
    private int commentcount;

    @Builder
    public ArticleResponseDto(Long id, String title, String content, LocalDateTime createdDate, LocalDateTime modifiedDate, Member member, int viewcount, List<BoardImage> boardImages, List<Comment> comments , int commentcount ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.member = member;
        this.viewcount = viewcount;
        // 이미지 파일이 Null 아닐 시 url 리스트 생성
        if (boardImages != null) {
            this.imageUrls = boardImages.stream()
                    .map(BoardImage::getUrl)
                    .collect(Collectors.toList());
        } else {
            this.imageUrls = Collections.emptyList();
        }
        this.comments = comments;
        this.commentcount = commentcount;
    }

}
