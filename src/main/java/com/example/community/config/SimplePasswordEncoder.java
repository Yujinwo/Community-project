package com.example.community.config;

import org.springframework.security.crypto.password.PasswordEncoder;

public class SimplePasswordEncoder implements PasswordEncoder {

    // 패스워드 인코딩
    @Override
    public String encode(CharSequence rawPassword){
        return rawPassword.toString();
    }

    // 원본 패스워드와 인코딩 패스워드와 일치 비교
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }
}
