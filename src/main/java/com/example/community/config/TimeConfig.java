package com.example.community.config;
import com.example.community.util.TimeUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

    @Bean
    public TimeUtil timeUtil() {
        return new TimeUtil();
    }
}
