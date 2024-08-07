package com.example.community.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyArticleResponseDto {
    private Long id;
    private String title;
    private String createdDate;
}
