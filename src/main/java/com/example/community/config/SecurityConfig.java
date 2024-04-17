package com.example.community.config;

import com.example.community.exception.UserLoginFailHandler;
import com.example.community.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return  (web) -> web.ignoring()
                .requestMatchers("/css/**","/images/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        requestCache.setMatchingRequestParameterName(null);

        http
                .csrf((csrfConfig) ->
                        csrfConfig.disable()
                )
                .requestCache(request -> request.requestCache(requestCache))
                .headers((headerConfig) ->
                         headerConfig.frameOptions(frameOptionsConfig ->
                                 frameOptionsConfig.disable()
                         )
                )
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                .requestMatchers(PathRequest.toH2Console()).permitAll()
                                .requestMatchers("/article/detail/*","/").permitAll()
                                .requestMatchers("/article/update/*","/article/write","/article/delete").hasRole("USER")
                                .requestMatchers("/admin").hasRole("ADMIN")
                                .requestMatchers("/login","/join").anonymous()
                                .anyRequest().permitAll()
                )
                .formLogin((formLogin) ->
                        formLogin.loginPage("/login")
                                .usernameParameter("email")
                                .passwordParameter("userpw")
                                .loginProcessingUrl("/login/proc")
                                .failureHandler(userLoginFailHandler)
                                .defaultSuccessUrl("/")
                )
                .logout((logout) ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login")
                )
                .userDetailsService(myUserDetailsService)
                .oauth2Login( (oauth2Login) ->
                        oauth2Login.userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint.userService(customOAuth2UserService)
                        )
                                .loginPage("/login")
                );

        return http.build();


    }


}
