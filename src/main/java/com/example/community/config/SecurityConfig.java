package com.example.community.config;

import com.example.community.exception.UserLoginFailHandler;
import com.example.community.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    UserDetailsService myUserDetailsService;

    @Autowired
    UserLoginFailHandler userLoginFailHandler;

    @Autowired
    CustomOAuth2UserService customOAuth2UserService;


    // 패스워드 암호화
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // css,images 폴더 권한 무시
    @Bean
    public WebSecurityCustomizer configure() {
        return  (web) -> web.ignoring()
                .requestMatchers("/css/**","/images/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // RequestParame 매개변수 설정 Null
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);

        http
                // csrf 토큰 비활성화
                .csrf((csrfConfig) ->
                        csrfConfig.disable()
                )
                .requestCache(request -> request.requestCache(requestCache))
                // X-Frame-Options 헤더 비활성화
                .headers((headerConfig) ->
                         headerConfig.frameOptions(frameOptionsConfig ->
                                 frameOptionsConfig.disable()
                         )
                )
                // Url 권한 설정
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers("/article/detail/*","/").permitAll()
                                .requestMatchers("/article/update/*","/article/write","/article/delete").hasRole("USER")
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .requestMatchers("/login","/join").permitAll()
                                .anyRequest().permitAll()
                )
                // form 로그인 설정
                .formLogin((formLogin) ->
                        formLogin.loginPage("/login")
                                .usernameParameter("email")
                                .passwordParameter("userpw")
                                .loginProcessingUrl("/login/proc")
                                // 로그인 실패시 핸들러 설정
                                .failureHandler(userLoginFailHandler)
                                .defaultSuccessUrl("/")
                )
                // UserDetails 서비스 설정
                .userDetailsService(myUserDetailsService)
                // oauth2.0 로그인 설정
                .oauth2Login( (oauth2Login) ->
                        oauth2Login.userInfoEndpoint(userInfoEndpoint ->
                                // oauth2.0 서비스 설정
                                userInfoEndpoint.userService(customOAuth2UserService)
                        )
                                .loginPage("/login")

                );


        return http.build();


    }


}
