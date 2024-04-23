package com.example.community.config;
import com.example.community.util.TimeUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

    // timeUtil(작성 및 수정 시간 계산해주는 유틸) Bean 주입
    @Bean
    public TimeUtil timeUtil() {
        return new TimeUtil();
    }
}
