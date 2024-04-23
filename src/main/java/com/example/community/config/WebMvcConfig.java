package com.example.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig  implements WebMvcConfigurer {
    private static final String UPLOAD_DIR = "file:./static/images/";

    // 정적 자원 리소스 Images 파일을 요청시 로컬 파일 시스템 경로로 매칭
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("file:/C:/community/community/src/main/resources/static/images/");

    }
}
