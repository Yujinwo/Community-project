package com.example.community.dto;

import lombok.Builder;
import lombok.Data;

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
}
