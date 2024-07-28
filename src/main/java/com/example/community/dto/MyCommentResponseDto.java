package com.example.community.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MyCommentResponseDto {
   private Long article_id;
   private String article_title;
   private String content;
   private String createdDate;
}
