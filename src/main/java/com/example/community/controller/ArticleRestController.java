package com.example.community.controller;

import com.example.community.dto.ArticleRequestDto;
import com.example.community.dto.CommentRequestDto;
import com.example.community.dto.ReplyRequestDto;
import com.example.community.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ArticleRestController {
    @Autowired
    ArticleService articleService;

    @PostMapping(value = "/article/write")
    public ResponseEntity<Map<String,String>> write(@Valid @RequestPart(value = "key") ArticleRequestDto articleRequestDto, @RequestPart(required = false,value = "value") List<MultipartFile> files) {
        articleService.write(articleRequestDto,files);
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);

    }
    @PostMapping("/article/delete")
    public ResponseEntity<Map<String,String>> delete(@Valid @RequestBody ArticleRequestDto articleRequestDto)
    {
        articleService.delete(articleRequestDto);
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

    @PostMapping("/article/update")
    public ResponseEntity<Map<String,String>> update(@Valid @RequestPart(value = "key") ArticleRequestDto articleRequestDto, @RequestPart(required = false,value = "value") List<MultipartFile> files)
    {
        articleService.update(articleRequestDto,files);
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "글 수정 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

    @PostMapping("/comment/write")
    public ResponseEntity<Map<String,String>> commentwrite(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        articleService.commentwrite(commentRequestDto);
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "댓글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

    @PostMapping("/comment/delete")
    public ResponseEntity<Map<String,String>> commentdelete(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        articleService.commentdelete(commentRequestDto);
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "댓글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

    @PostMapping("/reply/write")
    public ResponseEntity<Map<String,String>> replywrite(@Valid @RequestBody ReplyRequestDto ReplyRequestDto) {
        articleService.replywrite(ReplyRequestDto);
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "답글 작성 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

    @PostMapping("/reply/delete")
    public ResponseEntity<Map<String,String>> replydelete(@Valid @RequestBody ReplyRequestDto ReplyRequestDto) {
        articleService.replydelete(ReplyRequestDto);
        Map<String,String> responseJson = new HashMap<>();
        responseJson.put("message" , "답글 삭제 완료했습니다");
        return ResponseEntity.status(HttpStatus.OK).body(responseJson);
    }

}
