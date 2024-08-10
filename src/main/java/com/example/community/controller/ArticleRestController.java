package com.example.community.controller;

import com.example.community.dto.ArticleRequestDto;
import com.example.community.dto.BookmarkRequestDto;
import com.example.community.dto.CommentRequestDto;
import com.example.community.entity.Article;
import com.example.community.entity.Notification;
import com.example.community.service.ArticleService;
import com.example.community.service.NotificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class ArticleRestController {

    private final ArticleService articleService;
    private final NotificationService notificationService;

    @Autowired
    public ArticleRestController(ArticleService articleService, NotificationService notificationService) {
        this.articleService = articleService;
        this.notificationService = notificationService;
    }


    // 글 작성 기능
    @PostMapping("/api/article/write")
    public ResponseEntity<Map<String,String>> write(@Valid @RequestPart(value = "key") ArticleRequestDto articleRequestDto, @RequestPart(required = false,value = "value") List<MultipartFile> files) {
        if(articleRequestDto.getTags().size() > 10){
            Map<String,String> responseJson = new HashMap<>();
            responseJson.put("message" , "허용되지 않은 사이즈 입니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseJson);
        }

        // 글 작성 서비스 메서드에 요청 Dto, 요청 파일 전달
        articleService.write(articleRequestDto,files);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);

    }

    // 글 삭제 기능
    @DeleteMapping("/api/article/delete/{id}")
    public ResponseEntity<Map<String,String>> delete(@Valid @PathVariable Long id)
    {
        // 글 삭제 서비스 메서드에 요청 Dto 전달
        articleService.delete(id);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }


    // 즐겨찾기 기능
    @PostMapping("/api/article/bookmark")
    public ResponseEntity<Map<String,String>> bookmark(@RequestBody BookmarkRequestDto bookmarkRequestDto)
    {

        if(bookmarkRequestDto.getId() == null || bookmarkRequestDto.getType().isEmpty())
        {
            Map<String,String> responseMap = new HashMap<>();
            responseMap.put("message","비정상적인 데이터입니다.");
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        }
        else {
            String responseJson = articleService.setBookmark(bookmarkRequestDto.getId(),bookmarkRequestDto.getType());
            Map<String,String> responseMap = new HashMap<>();
            responseMap.put("message" , responseJson);
            if(responseJson.equals("올바른 데이터 접근이 아닙니다."))
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
            }
            return ResponseEntity.status(HttpStatus.OK).body(responseMap);
        }

    }

    // 글 수정 기능
    @PatchMapping("/api/article/update")
    public ResponseEntity<Map<String,String>> update(@Valid @RequestPart(value = "key") ArticleRequestDto articleRequestDto, @RequestPart(required = false,value = "value") List<MultipartFile> files)
    {
       // 글 수정 서비스 메서드에 요청 Dto, 요청 파일 전달
       Article article = articleService.update(articleRequestDto,files);
       // json 메세지 생성
       Map<String,String> responseJson = new HashMap<>();

       // article Entity가 null 일시
       if (article == null) {
           responseJson.put("message" , "제목과 내용을 수정해주세요");
       }
       else{
           responseJson.put("message" , "글 수정 완료했습니다");
       }
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }
    // 댓글 작성 기능
    @PostMapping("/api/comment/write")
    public ResponseEntity<Map<String,String>> commentwrite(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        // 댓글 작성 서비스 메서드에 요청 Dto 전달
        Notification notification = articleService.commentwrite(commentRequestDto);
        // SSE 댓글 메세지 전송
        notificationService.sendRealTimeNotification(notification);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "댓글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }
    // 댓글 삭제 기능
    @DeleteMapping("/api/comment/delete")
    public ResponseEntity<Map<String,String>> commentdelete(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        // 댓글 삭제 서비스 메서드에 요청 Dto 전달
        articleService.commentdelete(commentRequestDto);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "댓글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }
    // 대댓글 작성 기능
    @PostMapping("/api/reply/write")
    public ResponseEntity<Map<String,String>> replywrite(@Valid @RequestBody CommentRequestDto ReplyRequestDto) {
        // 대댓글 작성 서비스 메서드에 요청 Dto 전달
        articleService.replywrite(ReplyRequestDto);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "답글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }


}
