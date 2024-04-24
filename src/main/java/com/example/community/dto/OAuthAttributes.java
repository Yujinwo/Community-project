package com.example.community.dto;

import com.example.community.domain.member.Role;
import com.example.community.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class OAuthAttributes {
    // 사용자 정보 Map
    private Map<String, Object> attributes;
    // 인증된 사용자의 이름을 식별하기 위해 사용되는 키
    private String nameAttributeKey;
    // 사용자 닉네임
    private String name;
    // 사용자 이메일
    private String email;
    // 네이버 / 카카오 / 구글 로그인 할시 그에 맞는 메서드 실행
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        // 네이버 로그인 할시
        if("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        // 카카오 로그인 할시
        else if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        }
        // 구글 로그인 할 시
        return ofGoogle(userNameAttributeName, attributes);
    }
    // 구글 로그인 메서드
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                // attributes에서 name,email 가져오기
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
    // 네이버 로그인 메서드
    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        // attributes에서 response 속성 값 get
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        // response에서 name,email 가져오기
        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
    // 카카오 로그인 메서드
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        // attributes에서 kakao_account 속성 값 get
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        // attributes에서 properties 속성 값 get
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        // properties에서 nickname 가져오기
        // kakao_account에서 email 가져오기
        return OAuthAttributes.builder()
                .name((String) properties.get("nickname"))
                .email((String) kakao_account.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
    // dto -> Member Entity로 생성
    public Member toEntity() {
        return Member.builder()
                .email(email)
                .usernick(name)
                .role(Role.USER)
                .build();
    }
}