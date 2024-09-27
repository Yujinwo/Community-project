package com.example.community.dto;


import lombok.*;
// My 페이지 내 글 조회 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyArticleResponseDto {
    private Long id;
    private String title;
    private String createdDate;
}
