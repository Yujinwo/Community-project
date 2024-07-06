package com.example.community.config;


import com.example.community.service.MemberService;
import com.example.community.util.AuthenticationUtil;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    private final MemberService memberService;
    private final EntityManager em;

    public AppConfig(MemberService memberService, EntityManager em) {
        this.memberService = memberService;
        this.em = em;
    }

    // OAuth 인증 Member 유틸에 MemberService를 주입
    @Bean
    public AuthenticationUtil authenticationUtil() {
        return new AuthenticationUtil(this.em,this.memberService);
    }
}
