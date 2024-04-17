package com.example.community.controller;

import com.example.community.dto.ArticleResponseDto;
import com.example.community.dto.CommentRequestDto;
import com.example.community.dto.CommentResponseDto;
import com.example.community.entity.Article;
import com.example.community.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping("/")
    public String page(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<ArticleResponseDto> postsPages = articleService.index(pageable);
        int blockLimit = 3;
        int startPage = Math.max(1, postsPages.getPageable().getPageNumber() - blockLimit);
        int endPage = Math.min(postsPages.getPageable().getPageNumber()+4, postsPages.getTotalPages());

        model.addAttribute("article", postsPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "index";
    }
    @GetMapping("/article/write")
    public String write() {
        return "write";
    }

    @GetMapping("/article/detail/{id}")
    public String detail(@PageableDefault(page = 1) Pageable pageable , @PathVariable Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
        Article article = articleService.viewcount(id,request, response);
        Page<CommentResponseDto> comments = articleService.findCommentid(id,pageable);
        int blockLimit = 3;
        int startPage = Math.max(1, comments.getPageable().getPageNumber() - blockLimit);
        int endPage = Math.min(comments.getPageable().getPageNumber()+4, comments.getTotalPages());
        model.addAttribute("article",article.toDto());
        model.addAttribute("comment",comments);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "detail";
    }
    @GetMapping("/article/update/{id}")
    public String updatepage(@PathVariable Long id, Model model) {
        Article article = articleService.findById(id);
        model.addAttribute("article",article.toDto());
        return "update";
    }
}
