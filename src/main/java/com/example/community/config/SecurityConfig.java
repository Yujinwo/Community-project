package com.example.community.config;

import com.example.community.exception.UserLoginFailHandler;
import com.example.community.service.CustomOAuth2UserService;
import com.example.community.util.CookieUtill;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
@Component
public class SecurityConfig{

    private final UserDetailsService myUserDetailsService;
    private final UserLoginFailHandler userLoginFailHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    private final MyAuthenticationEntryPoint myAuthenticationEntryPoint;
    private final MyAccessDeniedHandler myAccessDeniedHandler;

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
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authConfig) throws Exception{
        // RequestParame 매개변수 설정 Null
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);

        http

                // csrf 토큰 비활성화
                .csrf((csrfConfig) ->
                        csrfConfig.disable()
                )
                .exceptionHandling((exceptionConfig) ->
                        exceptionConfig.authenticationEntryPoint(myAuthenticationEntryPoint).accessDeniedHandler(myAccessDeniedHandler)
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
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .requestMatchers("/login","/join").anonymous()
                                .requestMatchers("/","/article/detail/*","/articles/search","/api/userid/check","/api/usernick/check","/authentication-fail","/authorization-fail").permitAll()
                                .anyRequest().hasRole("USER")
                )
                .logout((logout) -> logout.logoutSuccessUrl("/login"))
                // form 로그인 설정
                .formLogin((formLogin) ->
                        formLogin.loginPage("/login")
                                .usernameParameter("email")
                                .passwordParameter("userpw")
                                .loginProcessingUrl("/login/proc")
                                .successHandler(customLoginSuccessHandler())  // 로그인 성공 핸들러 설정
                                // 로그인 실패시 핸들러 설정
                                .failureHandler(userLoginFailHandler)

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // 모든 경로에 대해서 CORS 설정을 적용

        return source;
    }


    @Bean
    public AuthenticationSuccessHandler customLoginSuccessHandler() {
        return (request, response, authentication) -> {
            String rememberMe = request.getParameter("rememberMe");
            if ("on".equals(rememberMe)) {
                // 쿠키 설정 예시
                CookieUtill.addCookie(response,"userId",request.getParameter("email"),30 * 24 * 60 * 60);
            }
            else {
                CookieUtill.removeCookie(response,"userId");
            }
            response.sendRedirect("/");  // 로그인 성공 후 리디렉션
        };
    }


}
