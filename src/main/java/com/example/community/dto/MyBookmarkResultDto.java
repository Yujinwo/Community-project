package com.example.community.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MyBookmarkResultDto {
    private List<MyBookmarkResponseDto> content;
    private int totalPages;
    private Boolean hasResult;
    private Boolean last;
    private Boolean first;
    private Boolean previous;
    private Boolean next;
    private int number;
    public static MyBookmarkResultDto createMyBookmarkResultDto(Page<MyBookmarkResponseDto> bymyBookmarklist) {
        return MyBookmarkResultDto.builder().first(bymyBookmarklist.isFirst()).last(bymyBookmarklist.isLast()).hasResult(bymyBookmarklist.hasContent()).previous(bymyBookmarklist.hasPrevious()).next(bymyBookmarklist.hasNext()).content(bymyBookmarklist.getContent()).number(bymyBookmarklist.getNumber()).totalPages(bymyBookmarklist.getTotalPages()).build();
    }
}
