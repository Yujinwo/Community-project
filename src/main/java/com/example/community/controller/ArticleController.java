package com.example.community.controller;

import com.example.community.dto.ArticleResponseDto;
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
        // 페이지 데이터 불러오기
        Page<ArticleResponseDto> postsPages = articleService.index(pageable);
        // 페이지 게시글 최대 수 설정
        int blockLimit = 3;
        // 시작 페이지
        int startPage = Math.max(1, postsPages.getPageable().getPageNumber() - blockLimit);
        // 마지막 페이지
        int endPage = Math.min(postsPages.getPageable().getPageNumber()+4, postsPages.getTotalPages());
        // 뷰에 데이터 전달
        model.addAttribute("article", postsPages);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "index";
    }

    // 글 작성 페이지
    @GetMapping("/article/write")
    public String write() {
        return "write";
    }


    // 글 상세 페이지
    @GetMapping("/article/detail/{id}")
    public String detail(@PageableDefault(page = 1) Pageable pageable , @PathVariable Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
        // 조회수 1 증가하고 글 불러오기
        Article article = articleService.viewcount(id,request, response);
        // 글 댓글 불러오기
        Page<CommentResponseDto> comments = articleService.findCommentid(id,pageable);
        // 댓글 최대 개수 설정
        int blockLimit = 3;
        // 시작 페이지
        int startPage = Math.max(1, comments.getPageable().getPageNumber() - blockLimit);
        // 마지막 페이지
        int endPage = Math.min(comments.getPageable().getPageNumber()+4, comments.getTotalPages());

        // 뷰에 데이터 전달
        model.addAttribute("article",article.toDto());
        model.addAttribute("comment",comments);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "detail";
    }

    // 글 수정 페이지
    @GetMapping("/article/update/{id}")
    public String updatepage(@PathVariable Long id, Model model) {
        // 글 불러오기
        Article article = articleService.findById(id);
        // 뷰에 데이터 전달
        model.addAttribute("article",article.toDto());
        return "update";
    }
}
