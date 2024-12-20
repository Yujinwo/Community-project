package com.example.community.controller;

import com.example.community.config.CustomUserDetails;
import com.example.community.util.CookieUtill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MemberController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error",required = false) String error, @RequestParam(value = "exception",required = false) String exception, Model model,HttpServletRequest request) {
    // 쿠키에 저장되어 있는 ID 불러오기
        String userId = CookieUtill.getCookieValue(request,"userId");
    // ID 있을시 ID 저장 버튼 활성화 / 없을시 버튼 비활성화
    if(userId != null) {
        model.addAttribute("rememberMe", true);
        model.addAttribute("userId", userId);
    }
    else {
        model.addAttribute("rememberMe",false);
    }

    // 로그인 실패 시 에러 메세지를 뷰에 전달
    model.addAttribute("error",error);
    model.addAttribute("exception",exception);
    return "user_login";
    }

    @GetMapping("/authentication-fail")
    public String redirect_authentication_fail() {
        return "authentication_fail";
    }
    @GetMapping("/authorization-fail")
    public String redirect_authorization_fail() {
        return "authorization_fail";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Securoty Context에 저장한 인증 객체 불러오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
                // 로그아웃
                new SecurityContextLogoutHandler().logout(request,response,authentication);
        }
        return "redirect:/login";
    }

    // 회원가입 페이지
    @GetMapping("/join")
    public String join() {
        return "user_join";

    }

}
