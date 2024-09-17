package com.example.community.dto;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyArticleResponseDto {
    private Long id;
    private String title;
    private String createdDate;
}
