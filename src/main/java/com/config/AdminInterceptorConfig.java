package com.config;

import com.filter.LoginInterceptor;
import com.filter.AdminInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author lala
 */

@Configuration
public class AdminInterceptorConfig extends WebMvcConfigurerAdapter {

    String[] admin = {"/root"};
    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(adminInterceptor())
                .addPathPatterns(admin);

        interceptorRegistry.addInterceptor(loginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/other");

        super.addInterceptors(interceptorRegistry);
    }

    @Bean
    public AdminInterceptor adminInterceptor() {
        return new AdminInterceptor();
    }

    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

}
