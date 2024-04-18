package com.example.community.config;


import com.example.community.service.MemberService;
import com.example.community.util.AuthenticationUtil;
import com.example.community.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final MemberService memberService;

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
