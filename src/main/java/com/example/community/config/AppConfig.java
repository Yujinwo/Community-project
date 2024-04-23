package com.example.community.config;


import com.example.community.service.MemberService;
import com.example.community.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final MemberService memberService;

    // OAuth 인증 Member 유틸에 MemberService를 주입
    @Autowired
    public AppConfig(MemberService memberService) {
        this.memberService = memberService;
        AuthenticationUtil.setMemberService(memberService);
    }

    @Bean
    public AuthenticationUtil authenticationUtil() {
        return new AuthenticationUtil();
    }
}
