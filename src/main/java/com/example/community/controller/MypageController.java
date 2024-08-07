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

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MypageController {

    private final ArticleService articleService;
    private final MemberRepository memberRepository;
    private final AuthenticationUtil authenticationUtil;
    @GetMapping("/mypage")
    public String mypage(@RequestParam(name = "userid",defaultValue = "0") Long userid,@RequestParam(name = "type",required = true,defaultValue = "article_list") String type,Model model,Pageable pageable) {

        Member optionalUser = memberRepository.findById(userid).orElse(authenticationUtil.getCurrentMember());
        model.addAttribute("user",optionalUser);

        if(type.equals("article_list"))
        {
            Member optionalArticleUser = memberRepository.findById(userid).orElse(authenticationUtil.getCurrentMember());
            // 글 댓글 불러오기
            MyArticleResultDto articles = articleService.findMyArticleList(pageable,optionalArticleUser);
            int startPage = Math.max(1, articles.getNumber() - 3);
            int endPage = Math.min(articles.getNumber()+4, articles.getTotalPages());

            // 뷰에 데이터 전달
            model.addAttribute("article",articles.getContent());
            model.addAttribute("pageable",articles);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            return "mypagearticle";
        }
        else if(type.equals("comment_list")) {
            Member optionalCommentUser = memberRepository.findById(userid).orElse(authenticationUtil.getCurrentMember());
            // 댓글 댓글 불러오기
            MyCommentResultDto comments = articleService.findMyCommentList(pageable,optionalCommentUser);
            int startPage = Math.max(1, comments.getNumber() - 3);
            int endPage = Math.min(comments.getNumber()+4, comments.getTotalPages());

            // 뷰에 데이터 전달
            model.addAttribute("comment",comments.getContent());
            model.addAttribute("pageable",comments);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            return "mypagecomment";
        }
        else if(type.equals("bookmark_list")){
            // 글 댓글 불러오기
            MyBookmarkResultDto bookmarks = articleService.findMyBookmarkList(pageable);
            int startPage = Math.max(1, bookmarks.getNumber() - 3);
            int endPage = Math.min(bookmarks.getNumber()+4, bookmarks.getTotalPages());

            // 뷰에 데이터 전달
            model.addAttribute("bookmark",bookmarks.getContent());
            model.addAttribute("pageable",bookmarks);
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
