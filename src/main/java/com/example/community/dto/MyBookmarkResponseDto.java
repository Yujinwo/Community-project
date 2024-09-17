package com.example.community.dto;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyBookmarkResponseDto {
    private Long bookmark_id;
    private Long article_id;
    private String article_title;
}
