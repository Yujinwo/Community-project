package com.example.community.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class MyCommentResultDto {
   private List<MyCommentResponseDto> content;
   private int totalPages;
   private Boolean hasResult;
   private Boolean last;
   private Boolean first;
   private Boolean previous;
   private Boolean next;
   private int number;
   public static MyCommentResultDto createMyCommentResultDto(Page<MyCommentResponseDto> bymyCommentlist) {
      return MyCommentResultDto.builder().first(bymyCommentlist.isFirst()).last(bymyCommentlist.isLast()).hasResult(bymyCommentlist.hasContent()).previous(bymyCommentlist.hasPrevious()).next(bymyCommentlist.hasNext()).content(bymyCommentlist.getContent()).number(bymyCommentlist.getNumber()).totalPages(bymyCommentlist.getTotalPages()).build();
   }
}
