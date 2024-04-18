package com.example.community.util;
import com.example.community.config.CustomUserDetails;
import com.example.community.entity.Member;
import com.example.community.service.MemberService;
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

import java.util.Map;

public class AuthenticationUtil {

    private static MemberService memberService;

    @Autowired
    public static void setMemberService(MemberService memberService) {
        AuthenticationUtil.memberService = memberService;
    }

    public static Member getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null; // 사용자가 인증되지 않은 경우
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            return userDetails.getUserEntity(); // Spring Security로 인증된 사용자의 엔티티 가져오기
        } else if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            // OAuth2에서 사용자 정보를 가져와서 엔티티를 생성하거나 다른 작업을 수행합니다.
            String email = oauth2User.getAttribute("email");
            if (email != null) {
                Member member = memberService.findByEmail(email);
                if (member != null) {
                    return member;
                } else {
                    // 이메일로 회원을 찾을 수 없는 경우에 대한 처리를 수행합니다.
                    throw new UsernameNotFoundException("User with email " + email + " not found");
                }
            }
            else if (email == null) {
                Map<String, Object> kakao_account = (Map<String, Object>) oauth2User.getAttribute("kakao_account");
                String kakaoemail = (String) kakao_account.get("email");
                if(kakaoemail != null){
                    Member member = memberService.findByEmail(kakaoemail);
                    if (member != null) {
                        return member;
                    } else {
                        // 이메일로 회원을 찾을 수 없는 경우에 대한 처리를 수행합니다.
                        throw new UsernameNotFoundException("User with email " + kakaoemail + " not found");
                    }

                }
                else{
                    throw new IllegalArgumentException("Email attribute not found in OAuth2 user");
                }
            }
            else {
                // 이메일 정보가 없는 경우에 대한 처리를 수행합니다.
                throw new IllegalArgumentException("Email attribute not found in OAuth2 user");
            }
        } else {
            return null; // 다른 인증 형식일 경우
        }
    }
}