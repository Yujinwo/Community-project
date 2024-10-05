package com.example.community.controller;

import com.example.community.dto.NotificationResponseDto;
import com.example.community.dto.NotificationResultDto;
import com.example.community.entity.Member;
import com.example.community.service.NotificationService;
import com.example.community.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class NotificationRestController {

    private final NotificationService notificationService;
    private final AuthenticationUtil authenticationUtil;

    // SSE 알림 구독
    @GetMapping(path = "/api/notifications/subscribe", produces = "text/event-stream")
    public SseEmitter streamNotifications() {
        // 인증된 Member Entity 가져오기
        Member member = authenticationUtil.getCurrentMember();
        if(member != null){
            return notificationService.createEmitter(member.getId());
        }
        return null;
    }
    @GetMapping(path = "/api/notifications")
    public NotificationResultDto getnotifications(Pageable pageable) {
        return notificationService.getnotifications(pageable);
    }

    @DeleteMapping(path = "/api/notifications/{id}")
    public ResponseEntity<Map<String, String>> deletenotifications(@PathVariable Long id) {

        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();

        Optional<Object> optionalnotications = notificationService.deletenotications(id);
        if(optionalnotications.isEmpty())
        {
            responseJson.put("message" , "허용되지 않은 접근 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        responseJson.put("message","알림 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }
}