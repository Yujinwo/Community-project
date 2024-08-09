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

    private Long count;
    private List<NotificationResponseDto> content;
    private Boolean last;
    private int number;
}
