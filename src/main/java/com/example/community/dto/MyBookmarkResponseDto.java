package com.example.community.dto;


import lombok.*;
// My 페이지 내 즐겨찾기 조회 DTO
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyBookmarkResponseDto {
    private Long bookmark_id;
    private Long article_id;
    private String article_title;
}
