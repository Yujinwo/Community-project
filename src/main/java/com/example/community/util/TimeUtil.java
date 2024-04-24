package com.example.community.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {

    public static String calculateTimeAgo(LocalDateTime createdTime) {
        // 생성된 시간이 null인 경우 "Unknown"을 반환합니다.
        if (createdTime == null) {
            return "Unknown";
        }
        // 현재 시간을 가져옵니다.
        LocalDateTime now = LocalDateTime.now();
        // 생성된 시간과 현재 시간 사이의 시간 간격을 계산합니다.
        Duration duration = Duration.between(createdTime, now);
        // 시간 간격이 1분 미만인 경우 "방금 전"을 반환합니다.
        if (duration.toMinutes() < 1) {
            return "방금 전";
        }
        // 시간 간격이 1시간 미만인 경우 분 단위로 표시합니다.
        else if (duration.toHours() < 1) {
            return duration.toMinutes() + " 분 전";
        }
        // 시간 간격이 1일 미만인 경우 시간 단위로 표시합니다.
        else if (duration.toDays() < 1) {
            return duration.toHours() + " 시간 전";
        }
        // 시간 간격이 1일 이상인 경우 일 단위로 표시합니다.
        else {
            return duration.toDays() + " 일 전";
        }
    }
}
