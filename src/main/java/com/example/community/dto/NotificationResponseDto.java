package com.example.community.dto;


import lombok.*;

import java.time.LocalDateTime;
// 알림 조회 DTO
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private Long id;
    // 댓글 작성자 닉네임
    private String writerNickname;
    // 댓글 내용
    private String commentContent;
    // 댓글 작성 시간
    private String commentCreatetime;
    // 글 제목
    private String articleTitle;
    // 글 id
    private Long articleId;
    // 댓글 알림 받는자 닉네임
    private String receiverNickname;
}
