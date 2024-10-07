package com.example.community.entity;

import com.example.community.dto.NotificationResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Member writer;

    private Long articleFindId;
    private String articleTitle;

    private String message;

    public NotificationResponseDto changeNotificationDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String createdFormatDate = createdDate.format(formatter);
        return NotificationResponseDto.builder()
                .id(id)
                .articleTitle(articleTitle)
                .articleId(articleFindId)
                .writerNickname(writer.getUsernick())
                .commentCreatetime(createdFormatDate)
                .commentContent(message)
                .receiverNickname(receiver.getUsernick()).build();

    }




}
