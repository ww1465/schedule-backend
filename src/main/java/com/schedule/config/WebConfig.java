package com.schedule.config;

import com.schedule.interceptor.LoginInterceptor;
import com.schedule.mapper.UserMapper;
import com.schedule.mapper.UserTokenMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserTokenMapper userTokenMapper;
    private final UserMapper userMapper;

    public WebConfig(UserTokenMapper userTokenMapper, UserMapper userMapper) {
        this.userTokenMapper = userTokenMapper;
        this.userMapper = userMapper;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(userTokenMapper, userMapper))
                .addPathPatterns("/**")
                .excludePathPatterns("/api/wxlogin");
    }
}