package com.example.community.dto;

import com.example.community.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResultDto {
    private List<NotificationResponseDto> content;
    private Long count;
    private Boolean last;
    private int number;
    public static NotificationResultDto createNotificationResultDto(Page<NotificationResponseDto> notificationList) {
        return NotificationResultDto.builder().count(notificationList.getTotalElements()).last(notificationList.isLast()).number(notificationList.getNumber()).content(notificationList.getContent()).build();
    }
}
