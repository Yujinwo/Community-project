package com.example.community.controller;

import com.example.community.dto.ArticleRequestDto;
import com.example.community.dto.CommentRequestDto;
import com.example.community.entity.Article;
import com.example.community.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ArticleRestController {

    private final ArticleService articleService;
    @Autowired
    public ArticleRestController(ArticleService articleService) {
        this.articleService = articleService;
    }


    // 글 작성 기능
    @PostMapping(value = "/article/write")
    public ResponseEntity<Map<String,String>> write(@Valid @RequestPart(value = "key") ArticleRequestDto articleRequestDto, @RequestPart(required = false,value = "value") List<MultipartFile> files) {
        // 글 작성 서비스 메서드에 요청 Dto, 요청 파일 전달
        articleService.write(articleRequestDto,files);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);

    }

    // 글 삭제 기능
    @DeleteMapping("/article/delete")
    public ResponseEntity<Map<String,String>> delete(@Valid @RequestBody ArticleRequestDto articleRequestDto)
    {
        // 글 삭제 서비스 메서드에 요청 Dto 전달
        articleService.delete(articleRequestDto);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

    // 글 수정 기능
    @PatchMapping("/article/update")
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
    @PostMapping("/comment/write")
    public ResponseEntity<Map<String,String>> commentwrite(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        // 댓글 작성 서비스 메서드에 요청 Dto 전달
        articleService.commentwrite(commentRequestDto);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "댓글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }
    // 댓글 삭제 기능
    @DeleteMapping("/comment/delete")
    public ResponseEntity<Map<String,String>> commentdelete(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        // 댓글 삭제 서비스 메서드에 요청 Dto 전달
        articleService.commentdelete(commentRequestDto);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "댓글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }
    // 대댓글 작성 기능
    @PostMapping("/reply/write")
    public ResponseEntity<Map<String,String>> replywrite(@Valid @RequestBody CommentRequestDto ReplyRequestDto) {
        // 대댓글 작성 서비스 메서드에 요청 Dto 전달
        articleService.replywrite(ReplyRequestDto);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "답글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }
    // 대댓글 삭제 기능
    @DeleteMapping("/reply/delete")
    public ResponseEntity<Map<String,String>> replydelete(@Valid @RequestBody CommentRequestDto ReplyRequestDto) {
        // 대댓글 삭제 서비스 메서드에 요청 Dto 전달
        articleService.replydelete(ReplyRequestDto);
        // json 메세지 생성
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "답글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

}
