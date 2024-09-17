package com.example.community.dto;


import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDto {
    private String writerNickname;
    private String commentContent;
    private String commentCreatetime;
    private String articleTitle;
    private Long articleId;
    private String receiverNickname;
}
