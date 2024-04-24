package com.example.community.service;


import com.example.community.dto.OAuthAttributes;
import com.example.community.entity.Member;
import com.example.community.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService{

    @Autowired
    MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest){
        // OAuth 2.0 사용자 정보를 로드하여 OAuth2User 객체를 생성합니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // OAuth2User에서 사용자의 속성(attributes)을 가져옵니다.
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 사용자의 클라이언트 등록 ID를 가져옵니다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // 사용자의 이름 속성(attribute) 이름을 가져옵니다.
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // OAuthAttributes 객체를 생성하여 사용자의 속성과 관련된 정보를 캡슐화합니다.
        OAuthAttributes oAuthattributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);
        // OAuthAttributes를 저장하거나 업데이트합니다.
        saveOrUpdate(oAuthattributes);
        return new DefaultOAuth2User(
                // 사용자에게 부여할 권한을 지정합니다.
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                // 사용자의 속성(attributes)을 제공합니다.
                oAuthattributes.getAttributes(),
                // 사용자의 이름 속성(attribute) 키를 지정합니다.
                oAuthattributes.getNameAttributeKey()
        );
    }
    private Member saveOrUpdate(OAuthAttributes attributes) {
        // 인증된 정보로 회원을 생성한다.
        return memberRepository.save(attributes.toEntity());
    }
}