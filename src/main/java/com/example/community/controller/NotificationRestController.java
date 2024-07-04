package com.example.community.controller;

import com.example.community.dto.NotificationResponseDto;
import com.example.community.dto.NotificationResultDto;
import com.example.community.entity.Member;
import com.example.community.service.NotificationService;
import com.example.community.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationRestController {

    private final NotificationService notificationService;

    @GetMapping(path = "/notifications", produces = "text/event-stream")
    public SseEmitter streamNotifications() {
        // 인증된 Member Entity 가져오기
        Member member = AuthenticationUtil.getCurrentMember();
        if(member != null) {
            return notificationService.createEmitter(member);
        }
        else {
            return null;
        }

    }
    @GetMapping(path = "/notifications/list")
    public NotificationResultDto getnotifications() {
        return notificationService.getnotifications();
    }
}