package com.example.community.dto;

import com.example.community.entity.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

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


    @Builder
    public ArticleindexResponseDto(Long id, String title, String content, LocalDateTime createdDate, LocalDateTime modifiedDate, Member member, int viewcount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.member = member;
        //멤버 프록시 강제 초기화
        member.getUserpw();
        this.viewcount = viewcount;
    }

}
