package com.example.community.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MyBookmarkResponseDto {
    private Long bookmark_id;
    private Long article_id;
    private String article_title;
}
