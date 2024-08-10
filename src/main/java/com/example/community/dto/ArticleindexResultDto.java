package com.example.community.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleindexResultDto {
    private List<ArticleindexResponseDto> content;
    private int totalPages;
    private Boolean hasResult;
    private Boolean last;
    private Boolean first;
    private Boolean previous;
    private Boolean next;
    private int number;

    public static ArticleindexResultDto createArticleindexResultDto(Page<ArticleindexResponseDto> articleDtos) {
     return  ArticleindexResultDto.builder().first(articleDtos.isFirst()).last(articleDtos.isLast()).next(articleDtos.hasNext()).number(articleDtos.getNumber()).content(articleDtos.getContent()).previous(articleDtos.hasPrevious()).hasResult(articleDtos.hasContent()).totalPages(articleDtos.getTotalPages()).build();
    }
}
