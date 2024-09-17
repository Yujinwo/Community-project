package com.example.community.dto;


import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyArticleResultDto {
    private List<MyArticleResponseDto> content;
    private int totalPages;
    private Boolean hasResult;
    private Boolean last;
    private Boolean first;
    private Boolean previous;
    private Boolean next;
    private int number;
    public static MyArticleResultDto createMyArticleResultDto(Page<MyArticleResponseDto> bymyArticlelist) {
        return MyArticleResultDto.builder().first(bymyArticlelist.isFirst()).last(bymyArticlelist.isLast()).hasResult(bymyArticlelist.hasContent()).previous(bymyArticlelist.hasPrevious()).next(bymyArticlelist.hasNext()).content(bymyArticlelist.getContent()).number(bymyArticlelist.getNumber()).totalPages(bymyArticlelist.getTotalPages()).build();
    }
}
