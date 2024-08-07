package com.example.community.controller;

import com.example.community.dto.ArticleResponseDto;
import com.example.community.dto.ArticleindexResponseDto;
import com.example.community.dto.CommentResponseDto;
import com.example.community.entity.Article;
import com.example.community.entity.Bookmark;
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

import java.awt.print.Book;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ArticleController {


    private final ArticleService articleService;
    private final AuthenticationUtil authenticationUtil;


    @GetMapping("/")
    public String page(@RequestParam(value = "sort",defaultValue = "newest",required = true) String sort ,@PageableDefault(page = 1) Pageable pageable, Model model) {

        Page<ArticleindexResponseDto> page = articleService.index(pageable,sort);
        int startPage = Math.max(1, page.getPageable().getPageNumber() - 3);
        int endPage = Math.min(page.getPageable().getPageNumber()+4, page.getTotalPages());
        model.addAttribute("hasResults", page.hasContent());
        model.addAttribute("sort",sort);
        model.addAttribute("article",page);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "index";
    }


    @GetMapping("/articles/search")
    public String searchArticles(@RequestParam(value = "search",defaultValue = "newest",required = true) String search,@RequestParam(value = "sort",defaultValue = "newest",required = true) String sort,@RequestParam("query") String query, @RequestParam(value = "tagsearch",required = false,defaultValue = "false") Boolean tagsearch,Model model, @PageableDefault(page = 1)  Pageable pageable) {
            Page<ArticleindexResponseDto> page = articleService.searchArticles(query, pageable,tagsearch,sort,search);
            int startPage = Math.max(1, page.getPageable().getPageNumber() - 3);
            int endPage = Math.min(page.getPageable().getPageNumber()+4, page.getTotalPages());
            model.addAttribute("sort",sort);
            model.addAttribute("article", page);
            model.addAttribute("query", query);
            model.addAttribute("sort",search);
            model.addAttribute("hasResults", page.hasContent());
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);

        return "searchindex";
    }
    // 글 작성 페이지
    @GetMapping("/article/write")
    public String write() {
        return "write";
    }


    // 글 상세 페이지
    @GetMapping("/article/detail/{id}")
    public String detail(@PageableDefault(page = 1) Pageable pageable , @PathVariable("id") Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
        // 조회수 1 증가하고 글 불러오기
        Article article = articleService.viewcount(id,request, response);
        ArticleResponseDto articleResponseDto = ArticleResponseDto.builder().article(article).build();

        // 글 댓글 불러오기
        Page<CommentResponseDto> comments = articleService.findCommentid(id,pageable);
        if(authenticationUtil.getCurrentMember() != null)
        {
            // 즐겨찾기 현황 체크
            Boolean bookmark_state = articleService.checkBookmark(id);
            model.addAttribute("bookmark_state",bookmark_state);
        }

        // 페이지 최대 개수 설정
        int blockLimit = 3;
        // 시작 페이지
        int startPage = Math.max(1, comments.getPageable().getPageNumber() - blockLimit);
        // 마지막 페이지
        int endPage = Math.min(comments.getPageable().getPageNumber()+4, comments.getTotalPages());

        // 뷰에 데이터 전달
        model.addAttribute("article",articleResponseDto);
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
        ArticleResponseDto articleResponseDto = ArticleResponseDto.builder().article(article).build();
        // 뷰에 데이터 전달
        model.addAttribute("article",articleResponseDto);
        return "update";
    }
}
