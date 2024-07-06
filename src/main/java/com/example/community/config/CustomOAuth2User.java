package com.example.community.config;

import com.example.community.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User extends DefaultOAuth2User {
    private Member member;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            Member member) {
        super(authorities, attributes, nameAttributeKey);
        this.member = member;
    }

    public Member getMemberEntity() {
        return member;
    }
}
