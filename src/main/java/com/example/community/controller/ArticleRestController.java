package com.example.community.controller;


import com.example.community.dto.ArticleDto;
import com.example.community.entity.Article;
import com.example.community.repository.ArticleRepository;
import com.example.community.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArticleRestController {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleService articleService;

    @PostMapping("/article/write")
    public ResponseEntity<Article> write(@RequestBody ArticleDto articledto) {
        Article article = articleService.write(articledto);
        return ResponseEntity.status(HttpStatus.OK).body(article);

    }

    @PostMapping("/article/delete")
    public ResponseEntity<Article> delete(@RequestBody ArticleDto articleDto)
    {
        articleService.delete(articleDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/article/update")
    public ResponseEntity<Article> update(@RequestBody ArticleDto articleDto)
    {
        Article article = articleService.update(articleDto);
        return ResponseEntity.status(HttpStatus.OK).body(article);
    }


}
