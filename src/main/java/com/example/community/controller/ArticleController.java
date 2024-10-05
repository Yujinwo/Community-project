package com.example.community.controller;

import com.example.community.dto.*;
import com.example.community.entity.Article;
import com.example.community.entity.Bookmark;
import com.example.community.entity.Member;
import com.example.community.repository.BookmarkRepository;
import com.example.community.service.ArticleService;
import com.example.community.util.AuthenticationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.print.Book;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ArticleController {


    private final ArticleService articleService;
    private final AuthenticationUtil authenticationUtil;

    // 홈
    @GetMapping("/")
    public String page(@RequestParam(value = "sort",defaultValue = "newest") String sort ,@PageableDefault(page = 1) Pageable pageable, Model model) {
        // 글 전체 페이징처리 조회
        ArticleindexResultDto page = articleService.findArticles(pageable,sort);
        // 최소 페이지
        int startPage = Math.max(1, page.getNumber() - 3);
        // 최대 페이지
        int endPage = Math.min(page.getNumber()+4, page.getTotalPages());
        model.addAttribute("sort",sort);
        model.addAttribute("pageable",page);
        model.addAttribute("article",page.getContent());
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "home";
    }

    // 검색 페이지
    @GetMapping("/articles")
    public String searchArticles(@RequestParam(value = "search",defaultValue = "titleAndcontent") String search,@RequestParam(value = "sort",defaultValue = "newest") String sort,@RequestParam(value = "query",defaultValue = "") String query, @RequestParam(value = "tagsearch",defaultValue = "false") Boolean tagsearch, Model model, @PageableDefault(page = 1)  Pageable pageable, RedirectAttributes redirectAttributes) {
        // 검색 키워드가 없으면
        if (query.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "검색 키워드를 입력해주세요");
            return "redirect:/"; // 홈으로 리다이렉트
        }
        // 글 검색 페이징 처리 조회
        ArticleindexResultDto page = articleService.searchArticles(query, pageable,tagsearch,sort,search);
        // 최소 페이지
        int startPage = Math.max(1, page.getNumber() - 3);
        // 최대 페이지
        int endPage = Math.min(page.getNumber()+4, page.getTotalPages());
        model.addAttribute("sort",sort);
        model.addAttribute("pageable",page);
        model.addAttribute("article",page.getContent());
        // 검색 조건
        model.addAttribute("search",search);
        // 검색 키워드
        model.addAttribute("query", query);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "article_search";
}
        // 글 작성 페이지
        @GetMapping("/articles/new")
        public String write() {
            return "article_write";
        }


    // 글 상세 페이지
    @GetMapping("/articles/{id}")
    public String detail(@PageableDefault(page = 1) Pageable pageable , @PathVariable("id") Long id, Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        // 조회수 1 증가하고 글 불러오기
        Optional<Article> article = articleService.viewcount(id,request, response);
        // 글 조회 실패시
        if (article.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "조회할 게시글이 존재하지 않습니다");
            return "redirect:/"; // 홈으로 리다이렉트
        }
        // 댓글 전체 페이징처리 조회
        CommentResultDto comments = articleService.findCommentid(id,pageable);
        ArticleResponseDto articleResponseDto = ArticleResponseDto.builder().article(article.get()).commentcount(comments.getCount()).build();

        if(authenticationUtil.getCurrentMember() != null)
        {
            // 즐겨찾기 현황 불러오기
            Boolean bookmark_state = articleService.checkBookmark(id);
            model.addAttribute("bookmark_state",bookmark_state);
        }
        // 최소 페이지
        int startPage = Math.max(1, comments.getNumber() - 3);
        // 최대 페이지
        int endPage = Math.min(comments.getNumber()+4, comments.getTotalPages());

        // 뷰에 데이터 전달
        model.addAttribute("article",articleResponseDto);
        model.addAttribute("pageable",comments);
        model.addAttribute("comment",comments.getContent());
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "article_detail";
    }


    // 글 수정 페이지
    @GetMapping("/articles/{id}/edit")
    public String updatepage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Member user = authenticationUtil.getCurrentMember();
        // 유저 데이터가 없으면
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "유저 데이터가 존재하지 않습니다");
            return "redirect:/"; // 홈으로 리다이렉트
        }
        // 글 불러오기
        Optional<Article> article = articleService.findById(id);
        // 글 조회 실패시
        if (article.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "조회할 게시글이 존재하지 않습니다");
            return "redirect:/"; // 홈으로 리다이렉트
        }
        ArticleResponseDto articleResponseDto = ArticleResponseDto.builder().article(article.get()).build();
        // 뷰에 데이터 전달
        model.addAttribute("article",articleResponseDto);
        return "article_update";
    }
}
