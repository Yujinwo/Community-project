package com.example.community.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {

    public static String calculateTimeAgo(LocalDateTime createdTime) {
        if (createdTime == null) {
            return "Unknown";
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdTime, now);

        if (duration.toMinutes() < 1) {
            return "방금 전";
        } else if (duration.toHours() < 1) {
            return duration.toMinutes() + " 분 전";
        } else if (duration.toDays() < 1) {
            return duration.toHours() + " 시간 전";
        } else {
            return duration.toDays() + " 일 전";
        }
    }
}
