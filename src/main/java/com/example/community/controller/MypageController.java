package com.example.community.controller;

import com.example.community.dto.*;
import com.example.community.entity.Article;
import com.example.community.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MypageController {

    private final ArticleService articleService;
    @GetMapping("/mypage")
    public String mypage(@RequestParam(name = "type",required = true,defaultValue = "article_list") String type,Model model,Pageable pageable) {

        if(type.equals("article_list"))
        {
            // 글 댓글 불러오기
            Page<MyArticleResponseDto> articles = articleService.findMyArticleList(pageable);
            // 페이지 최대 개수 설정
            int blockLimit = 3;
            // 시작 페이지
            int startPage = Math.max(1, articles.getPageable().getPageNumber() - blockLimit);
            // 마지막 페이지
            int endPage = Math.min(articles.getPageable().getPageNumber()+4, articles.getTotalPages());

            // 뷰에 데이터 전달
            model.addAttribute("article",articles);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            return "mypagearticle";
        }
        else if(type.equals("comment_list")) {
            // 댓글 댓글 불러오기
            Page<MyCommentResponseDto> comments = articleService.findMyCommentList(pageable);
            // 페이지 최대 개수 설정
            int blockLimit = 3;
            // 시작 페이지
            int startPage = Math.max(1, comments.getPageable().getPageNumber() - blockLimit);
            // 마지막 페이지
            int endPage = Math.min(comments.getPageable().getPageNumber()+4, comments.getTotalPages());

            // 뷰에 데이터 전달
            model.addAttribute("comment",comments);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            return "mypagecomment";
        }
        else if(type.equals("bookmark_list")){
            // 글 댓글 불러오기
            Page<MyBookmarkResponseDto> bookmarks = articleService.findMyBookmarkList(pageable);
            // 페이지 최대 개수 설정
            int blockLimit = 3;
            // 시작 페이지
            int startPage = Math.max(1, bookmarks.getPageable().getPageNumber() - blockLimit);
            // 마지막 페이지
            int endPage = Math.min(bookmarks.getPageable().getPageNumber()+4, bookmarks.getTotalPages());

            // 뷰에 데이터 전달
            model.addAttribute("bookmark",bookmarks);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            return "mypagebookmark";
        }
        else if(type.equals("edit_profile")){
            return "mypageedit";
        }
        else {
            return "mypagearticle";
        }
    }



}
