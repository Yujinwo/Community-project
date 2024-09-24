package com.example.community.controller;

import com.example.community.dto.NotificationResponseDto;
import com.example.community.dto.NotificationResultDto;
import com.example.community.entity.Member;
import com.example.community.service.NotificationService;
import com.example.community.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationRestController {

    private final NotificationService notificationService;
    private final AuthenticationUtil authenticationUtil;

    @GetMapping(path = "/api/notifications/subscribe", produces = "text/event-stream")
    public SseEmitter streamNotifications() {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        if(member != null){
            return notificationService.createEmitter(member.getId());
        }
        else {
            return null;
        }

    }
    @GetMapping(path = "/api/notifications")
    public NotificationResultDto getnotifications(Pageable pageable) {
        return notificationService.getnotifications(pageable);
    }
}