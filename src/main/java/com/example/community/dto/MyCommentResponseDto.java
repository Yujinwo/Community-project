package com.example.community.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCommentResponseDto {
   private Long article_id;
   private String article_title;
   private String content;
   private String createdDate;
}
