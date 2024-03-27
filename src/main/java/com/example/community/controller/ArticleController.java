package com.example.community.controller;

import com.example.community.dto.ArticleDto;
import com.example.community.entity.Article;
import com.example.community.repository.ArticleRepository;
import com.example.community.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleService articleService;

    @GetMapping("/article/paging")
    public String page(@PageableDefault(page = 1) Pageable pageable, Model model) {

        Page<ArticleDto> postsPages = articleService.index(pageable);

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

    @GetMapping("/article/{id}")
    public String detailpage(@PathVariable Long id, Model model) {

        Article article = articleRepository.findById(id).orElse(null);
        model.addAttribute("article",article.toDto());


        return "detail";
    }

    @GetMapping("/article/update/{id}")
    public String updatepage(@PathVariable Long id, Model model) {

        Article article = articleRepository.findById(id).orElse(null);
        model.addAttribute("article",article.toDto());


        return "update";
    }
}
