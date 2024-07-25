package com.example.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MypageController {
    @GetMapping("/mypage")
    public String mypage(@RequestParam(name = "type",required = true,defaultValue = "article") String type) {

        if(type.equals("article_list"))
        {
            return "mypagearticle";
        }
        else if(type.equals("comment_list")) {
            return "mypagecomment";
        }
        else if(type.equals("edit_profile")){
            return "mypageedit";
        }
        else {
            return "mypagearticle";
        }
    }



}
