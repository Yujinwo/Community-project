package com.example.community.service;

import com.example.community.dto.NotificationResponseDto;
import com.example.community.dto.NotificationResultDto;
import com.example.community.entity.Article;
import com.example.community.entity.Member;
import com.example.community.entity.Notification;
import com.example.community.repository.NotificationRepository;
import com.example.community.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final Map<Long, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    private final NotificationRepository notificationRepository;

    @Transactional
    public SseEmitter createEmitter(Member member) {
        try {
            Long userId = member.getId();
            SseEmitter emitter = vaildDuplicateEmitter(userId);
            if(emitter == null) {
                emitter = new SseEmitter(Long.MAX_VALUE);
                userEmitters.put(userId, emitter);

                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data("연결완료"));
                } catch (Exception e) {
                    log.info("에러 메세지 " + e.getMessage());
                    userEmitters.remove(userId);
                    // 예외 처리
                }

                emitter.onCompletion(() -> userEmitters.remove(userId));
                emitter.onTimeout(() -> userEmitters.remove(userId));
                emitter.onError((e) -> userEmitters.remove(userId));

                return emitter;
            }
            else {
                return emitter;
            }
        }
        catch (NullPointerException e) {
            return null;
        }
    }
    @Transactional
    // 생성된 Emitter가 있는지 확인
    public SseEmitter vaildDuplicateEmitter(Long userId) {
        SseEmitter emitter = userEmitters.get(userId);
        if (emitter == null) {
            return null;
        } else {
            return emitter;
        }

    }


    @Transactional
    public void sendNotification(Member receiver, Member writer, Article article,String message,boolean read) {
        Notification notification = Notification.
                                        builder()
                                        .receiver(receiver)
                                        .writer(writer)
                                        .article(article)
                                        .message(message)
                                        .read(read).build();
        Notification savedNotification = notificationRepository.save(notification);
        sendRealTimeNotification(savedNotification);
    }

    @Transactional
    private void sendRealTimeNotification(Notification notification) {
        SseEmitter emitter = userEmitters.get(notification.getReceiver().getId());
        if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(notification.getWriter().getUsernick() + " 님이 회원님이 작성한 " + notification.getArticle().getTitle() + "에 "  + notification.getMessage() + " 댓글을 작성했습니다."));
                } catch (Exception e) {
                    log.info(e.getMessage());
                    userEmitters.remove(notification.getReceiver().getId());
                    // 예외 처리
                }

        }
    }

    public NotificationResultDto getnotifications() {
        // 인증된 Member Entity 가져오기
        Member member = AuthenticationUtil.getCurrentMember();
        if(member != null)
        {
            List<NotificationResponseDto> notificationList = notificationRepository.findByNoticication(member).stream().map(n -> n.changeNotificationDto()).collect(Collectors.toList());

            return NotificationResultDto.builder().count(1).Result(notificationList).build();
        }
        return new NotificationResultDto();


    }
}