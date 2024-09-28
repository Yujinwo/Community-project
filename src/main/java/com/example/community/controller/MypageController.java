package com.example.community.controller;

import com.example.community.dto.*;
import com.example.community.entity.Article;
import com.example.community.entity.Member;
import com.example.community.repository.MemberRepository;
import com.example.community.service.ArticleService;
import com.example.community.util.AuthenticationUtil;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MypageController {

    private final ArticleService articleService;
    private final AuthenticationUtil authenticationUtil;
    @GetMapping("/mypage")
    public String mypage(@RequestParam(name = "type",defaultValue = "article_list") String type,Model model,Pageable pageable, RedirectAttributes redirectAttributes) {
        Member user = authenticationUtil.getCurrentMember();
        // 유저 데이터가 없으면
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "유저 데이터가 존재하지 않습니다.");
            return "redirect:/"; // 홈으로 리다이렉트
        }
        // 내가 작성한 글 리스트
        if(type.equals("article_list"))
        {
            // 내 글 전체 페이징처리 조회
            MyArticleResultDto articles = articleService.findMyArticleList(user,pageable);
            // 최소 페이지
            int startPage = Math.max(1, articles.getNumber() - 3);
            // 최대 페이지
            int endPage = Math.min(articles.getNumber()+4, articles.getTotalPages());

            // 뷰에 데이터 전달
            model.addAttribute("article",articles.getContent());
            model.addAttribute("pageable",articles);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            return "mypage_article";
        }
        // 내가 작성한 댓글 리스트
        else if(type.equals("comment_list")) {
            // 내 댓글 전체 페이징처리 조회
            MyCommentResultDto comments = articleService.findMyCommentList(user,pageable);
            // 최소 페이지
            int startPage = Math.max(1, comments.getNumber() - 3);
            // 최대 페이지
            int endPage = Math.min(comments.getNumber()+4, comments.getTotalPages());

            // 뷰에 데이터 전달
            model.addAttribute("comment",comments.getContent());
            model.addAttribute("pageable",comments);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            return "mypage_comment";
        }
        // 내가 즐겨찾기 한 글 리스트
        else if(type.equals("bookmark_list")){
            // 내 즐겨찾기 전체 페이징처리 조회
            MyBookmarkResultDto bookmarks = articleService.findMyBookmarkList(user,pageable);
            // 최소 페이지
            int startPage = Math.max(1, bookmarks.getNumber() - 3);
            // 최대 페이지
            int endPage = Math.min(bookmarks.getNumber()+4, bookmarks.getTotalPages());

            // 뷰에 데이터 전달
            model.addAttribute("bookmark",bookmarks.getContent());
            model.addAttribute("pageable",bookmarks);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            return "mypage_bookmark";
        }
        // 내 정보 수정
        else if(type.equals("edit_profile")){
            return "mypage_edit";
        }
        else {
            return "mypage_article";
        }
    }



}
