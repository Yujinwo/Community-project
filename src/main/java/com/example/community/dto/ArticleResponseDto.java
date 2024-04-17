package com.example.community.dto;

import com.example.community.entity.Article;
import com.example.community.entity.BoardImage;
import com.example.community.entity.Comment;
import com.example.community.entity.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ArticleResponseDto {


    @NotNull
    private Long id;

    @Size(min=1,max=60,message = "제목은 1~60자 이내로 작성해주세요")
    private String title;

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
    private List<String> imageUrls;
    private List<Comment> comments;

    @Builder   // 생성자를 만든 후 그 위에 @Builder 애노테이션 적용
    public ArticleResponseDto(Long id, String title, String content, LocalDateTime createdDate, LocalDateTime modifiedDate, Member member, int viewcount, List<BoardImage> boardImages,List<Comment> comments ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.member =member;
        this.viewcount = viewcount;
        if (boardImages != null) {
            this.imageUrls = boardImages.stream()
                    .map(BoardImage::getUrl)
                    .collect(Collectors.toList());
        } else {
            this.imageUrls = Collections.emptyList();
        }
        this.comments = comments;
    }

}
