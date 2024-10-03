package com.example.community.controller;

import com.example.community.dto.ArticleRequestDto;
import com.example.community.dto.CommentRequestDto;
import com.example.community.entity.Article;
import com.example.community.entity.Notification;
import com.example.community.service.ArticleService;
import com.example.community.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ArticleRestController {

    private final ArticleService articleService;
    private final NotificationService notificationService;

    @Autowired
    public ArticleRestController(ArticleService articleService, NotificationService notificationService) {
        this.articleService = articleService;
        this.notificationService = notificationService;
    }


    // 글 작성
    @PostMapping("/api/articles")
    public ResponseEntity<Map<String,String>> write(@Valid @RequestPart(value = "key") ArticleRequestDto articleRequestDto, @RequestPart(required = false,value = "value") List<MultipartFile> files) {
        int filesize = 0;
        Map<String,String> responseJson = new HashMap<>();
        // 파일 확장자 검증
        if(files != null)
        {
            filesize = files.size();
        }

        // 이미지 + 파일이 2개 이상일 시
        if(articleRequestDto.getImageUrls().size() + filesize > 2){
            responseJson.put("message" , "허용되지 않은 사이즈 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        // 유저 인증 및 글 조회 실패 시
        Optional<Object> optionalarticle = articleService.write(articleRequestDto, files);
        if(optionalarticle.isEmpty())
        {
            responseJson.put("message" , "허용되지 않은 접근 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        responseJson.put("message" , "글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);

    }


    // 글 수정
    @PutMapping("/api/articles")
    public ResponseEntity<Map<String,String>> update(@Valid @RequestPart(value = "key") ArticleRequestDto articleRequestDto, @RequestPart(required = false,value = "value") List<MultipartFile> files)
    {
        Optional<Object> optionalarticle = articleService.update(articleRequestDto,files);

        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        if (optionalarticle.isEmpty()) {
            responseJson.put("message" , "허용되지 않은 접근 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        responseJson.put("message" , "글 수정 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

    // 글 삭제
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Map<String,String>> delete(@PathVariable Long id)
    {
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        // 유저 인증 및 글 조회 실패 시
        Optional<Object> optionalarticle = articleService.delete(id);
        if(optionalarticle.isEmpty())
        {
            responseJson.put("message" , "허용되지 않은 접근 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        responseJson.put("message" , "글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }


    // 즐겨찾기 추가
    @PostMapping("/api/bookmarks/{id}")
    public ResponseEntity<Map<String,String>> setbookmark(@PathVariable Long id)
    {
           Map<String,String> responseMap = new HashMap<>();
           Optional<Object> optionalbookmark = articleService.setBookmark(id);
           if(optionalbookmark.isEmpty())
           {
               responseMap.put("message" , "허용되지 않은 접근 입니다.");
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
           }
           responseMap.put("message" , "즐겨 찾기 등록 완료했습니다.");
           return ResponseEntity.status(HttpStatus.OK).body(responseMap);

    }
    // 즐겨찾기 삭제
    @DeleteMapping("/api/bookmarks/{id}")
    public ResponseEntity<Map<String,String>> deletebookmark(@PathVariable Long id)
    {

        Map<String,String> responseMap = new HashMap<>();
        Optional<Object> optionalbookmark = articleService.deleteBookmark(id);
        if(optionalbookmark.isEmpty())
        {
            responseMap.put("message" , "허용되지 않은 접근 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        }
        responseMap.put("message" , "즐겨 찾기 삭제 완료했습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(responseMap);

    }

    // 댓글 작성
    @PostMapping("/api/comments")
    public ResponseEntity<Map<String,String>> commentwrite(@Valid @RequestBody CommentRequestDto commentRequestDto) {

        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        Optional<Object> notification = articleService.commentwrite(commentRequestDto);

        if(notification.isEmpty())
        {
            responseJson.put("message" , "허용되지 않은 접근 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }

        // SSE 댓글 메세지 전송
        Optional<Object> optionalobject = notificationService.sendRealTimeNotification((Notification) notification.get());
        if(optionalobject.isEmpty())
        {
            responseJson.put("message" , "전송 실패 했습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        responseJson.put("message" , "댓글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Map<String,String>> commentdelete(@PathVariable Long id) {

        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        Optional<Object> deletedcomment = articleService.commentdelete(id);
        if(deletedcomment.isEmpty())
        {
            responseJson.put("message" , "허용되지 않은 접근 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }

        responseJson.put("message" , "댓글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

    // 대댓글 작성
    @PostMapping("/api/comments/replies")
    public ResponseEntity<Map<String,String>> replywrite(@Valid @RequestBody CommentRequestDto ReplyRequestDto) {
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        Optional<Object> reply =  articleService.replywrite(ReplyRequestDto);
        if(reply.isEmpty())
        {
            responseJson.put("message" , "허용되지 않은 접근 입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        responseJson.put("message" , "대댓글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }


}
