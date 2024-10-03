package com.example.community.config;

import com.example.community.entity.Bookmark;
import com.example.community.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User extends DefaultOAuth2User {
    private Member member;



    // OAuth2User 클래스를 커스텀해서 유저 정보도 저장
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

    public void changeMyProfile(String usernick) {
        this.member.setUsernick(usernick);
    }

    public void changeNoteBlock(Boolean noteblock) {
        this.member.setNoteblock(noteblock);
    }
    public void changeTemporaryblockdate(LocalDateTime temporaryblockdate) {
        this.member.setTemporaryblockdate(temporaryblockdate);
    }
}
