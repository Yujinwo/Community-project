package com.example.community.dto;

import lombok.*;
// My 페이지 내 댓글 조회 DTO
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
