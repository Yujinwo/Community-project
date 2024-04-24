package com.example.community.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;

// Spring의 컴포넌트로 등록되어 Spring 컨텍스트에서 관리됨
@Component
public class UserLoginFailHandler extends SimpleUrlAuthenticationFailureHandler {
    // 로그인 실패 시 호출되는 메서드 재정의
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException exception) throws IOException, ServletException {
        String errorMessage;
        // 예외의 종류에 따라 다른 에러 메시지 설정
        if (exception instanceof BadCredentialsException){
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해주세요.";
        }
        else if (exception instanceof InternalAuthenticationServiceException){
            errorMessage = "내부적으로 발생한 시스템 문제로 인해 요청을 처리할 수 없습니다. 관리자에게 문의하세요.";
        }
        else if ( exception instanceof UsernameNotFoundException) {
            errorMessage = "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요.";
            
        }
        else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorMessage = "인증 요청이 거부되었습니다. 관리자에게 문의하세요.";
        }
        else {
            errorMessage = "알 수 없는 이유로 로그인에 실패하였습니다 관리자에게 문의하세요.";
        }
        // 에러 메시지를 UTF-8 형식으로 인코딩
        errorMessage = URLEncoder.encode(errorMessage,"UTF-8");

        // 기본 로그인 실패 URL에 에러 메시지를 파라미터로 추가
        setDefaultFailureUrl("/login?error=true&exception=" + errorMessage);

        // 부모 클래스의 onAuthenticationFailure 메서드 호출하여 기본 동작 수행
        super.onAuthenticationFailure(request, response, exception);
    }
}
