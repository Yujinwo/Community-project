package com.example.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MemberController {

    // 로그인 페이지
    @GetMapping("/login")
    public String login(@RequestParam(value = "error",required = false) String error, @RequestParam(value = "exception",required = false) String exception, Model model) {
    // 로그인 실패 시 에러 메세지를 뷰에 전달
    model.addAttribute("error",error);
    model.addAttribute("exception",exception);
    return "/login";
    }

    // 회원가입 페이지
    @GetMapping("/join")
    public String join() {
        return "join";

    }

}
