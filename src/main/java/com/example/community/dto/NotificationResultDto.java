package com.example.community.dto;

import com.example.community.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResultDto {

    private int count;
    private List<NotificationResponseDto> Result;
}
