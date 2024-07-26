package com.example.community.util;
import com.example.community.config.CustomOAuth2User;
import com.example.community.config.CustomUserDetails;
import com.example.community.entity.Member;
import com.example.community.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class AuthenticationUtil {

    // Security / Oauth2.0 인증된 회원 정보를 온다.
    public Member getCurrentMember() {
        // 저장된 인증 객체를 가져온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 회원가 없을 시 || 회원이 인증되지 않은 경우 null을 return 한다
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        // 인증 객체의 principal 객체를 가져온다
        Object principal = authentication.getPrincipal();
        // principal 객체가 UserDetails의 인스턴스 일시
        if (principal instanceof UserDetails) {
            // Spring Security로 인증된 사용자의 엔티티 가져오기
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.getUserEntity();
        }
        // principal 객체가 OAuth2User의 인스턴스 일시
        else if (principal instanceof OAuth2User) {
            // OAuth2에서 회원 정보를 가져와서 엔티티를 생성하거나 다른 작업을 수행합니다.
            CustomOAuth2User oauth2User = (CustomOAuth2User) principal;
            // 회원 이메일 정보를 가져온다.
            Member member = oauth2User.getMemberEntity();
            if (member != null) {
                    return member;
            }
            else {
                // member 엔티티가 없을 경우에 예외 상황을 발생
                throw new IllegalArgumentException("Email attribute not found in OAuth2 user");
            }
        }
        else {
            return null;
        }
    }
}