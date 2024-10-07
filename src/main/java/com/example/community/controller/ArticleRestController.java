package com.example.community.controller;

import com.example.community.dto.ArticleRequestDto;
import com.example.community.dto.CommentRequestDto;
import com.example.community.dto.CommentUpdateDto;
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

import java.util.*;
import java.util.regex.Pattern;

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

        // 파일 사이즈 체크 / 파일 확장자 확인
        if(files != null)
        {
            filesize = files.size();

            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename().toLowerCase();
                if (fileName != null && !(fileName.endsWith(".png") || fileName.endsWith(".jpg"))) {
                    responseJson.put("message" , "허용되지 않은 확장자입니다");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
                }
            }
        }
        // 태그 길이 // 태그 중복 / 태그 특수문자 확인
        if(!articleRequestDto.getTags().isEmpty()) {
            Set<String> tagSet = new HashSet<>();

            for (String tag : articleRequestDto.getTags()) {
                if( !(tag.length() > 3 || tag.length() < 8) ) {
                    responseJson.put("message" , "태그 길이는 3자 이상 8자 이하이어야 합니다");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
                }
                if (tagSet.contains(tag)) {
                    responseJson.put("message" , "중복 태그는 허용되지 않습니다");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
                }
                tagSet.add(tag); // 태그를 Set에 추가
                // 특수문자 제거 정규 표현식
                String sanitizedValue = tag.replaceAll("[^a-zA-Z0-9가-힣]", "");
                // 자음 및 모음 패턴
                Pattern disallowedKoreanSounds = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ]");
                // 검사
                if (!sanitizedValue.equals(tag) || disallowedKoreanSounds.matcher(tag).find()) {
                    responseJson.put("message" , "특수문자와 자음/모음은 허용되지 않습니다");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
                }
            }
        }


        // 이미지 + 파일이 2개 이상일 시
        if(articleRequestDto.getImageUrls().size() + filesize > 2){
            responseJson.put("message" , "허용되지 않은 사이즈 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        // 유저 인증 및 글 조회 실패 시
        Optional<Object> optionalarticle = articleService.write(articleRequestDto, files);
        if(optionalarticle.isEmpty())
        {
            responseJson.put("message" , "허용되지 않은 접근 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        responseJson.put("message" , "글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);

    }


    // 글 수정
    @PutMapping("/api/articles")
    public ResponseEntity<Map<String,String>> update(@Valid @RequestPart(value = "key") ArticleRequestDto articleRequestDto, @RequestPart(required = false,value = "value") List<MultipartFile> files)
    {
        int filesize = 0;
        Map<String,String> responseJson = new HashMap<>();
        if(files != null)
        {
            filesize = files.size();
            // 파일 확장자 확인
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename().toLowerCase();
                if (fileName != null && !(fileName.endsWith(".png") || fileName.endsWith(".jpg"))) {
                    responseJson.put("message" , "허용되지 않은 확장자입니다");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
                }
            }
        }
        // 태그 길이 확인 // 태그 중복 확인
        if(!articleRequestDto.getTags().isEmpty()) {
            Set<String> tagSet = new HashSet<>();
            for (String tag : articleRequestDto.getTags()) {
                 if( !(tag.length() > 3 || tag.length() < 8) ) {
                     responseJson.put("message" , "태그 길이는 3자 이상 8자 이하이어야 합니다");
                     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
                 }
                if (tagSet.contains(tag)) {
                    responseJson.put("message" , "중복 태그는 허용되지 않습니다");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
                }
                tagSet.add(tag); // 태그를 Set에 추가
            }
        }

        // 이미지 + 파일이 2개 이상일 시
        if(articleRequestDto.getImageUrls().size() + filesize > 2){
            responseJson.put("message" , "허용되지 않은 사이즈 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        Optional<Object> optionalarticle = articleService.update(articleRequestDto,files);

        if (optionalarticle.isEmpty()) {
            responseJson.put("message" , "허용되지 않은 접근 입니다");
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
            responseJson.put("message" , "허용되지 않은 접근 입니다");
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
               responseMap.put("message" , "허용되지 않은 접근 입니다");
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
           }
           responseMap.put("message" , "즐겨 찾기 등록 완료했습니다");
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
            responseMap.put("message" , "허용되지 않은 접근 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMap);
        }
        responseMap.put("message" , "즐겨 찾기 삭제 완료했습니다");
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
            responseJson.put("message" , "허용되지 않은 접근 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        // SSE 댓글 메시지 전송
        Object notificationObject = notification.get(); // 객체 가져오기
        if (notificationObject instanceof Notification) {
            Notification notificationInstance = (Notification) notificationObject; // 안전한 캐스트
            notificationService.sendRealTimeNotification(notificationInstance); // 메서드 호출
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
            responseJson.put("message" , "허용되지 않은 접근 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }

        responseJson.put("message" , "댓글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }
    // 댓글 수정
    @PatchMapping("/api/comments")
    public ResponseEntity<Map<String,String>> commentupdate(@Valid @RequestBody CommentUpdateDto commentUpdateDto) {

        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        Optional<Object> updatedcomment = articleService.commentupdate(commentUpdateDto);
        if(updatedcomment.isEmpty())
        {
            responseJson.put("message" , "허용되지 않은 접근 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }

        responseJson.put("message" , "댓글 수정 완료했습니다");
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
            responseJson.put("message" , "허용되지 않은 접근 입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseJson);
        }
        responseJson.put("message" , "대댓글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }


}
