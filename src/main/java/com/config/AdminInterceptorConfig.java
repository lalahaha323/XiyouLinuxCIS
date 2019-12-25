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

    private String[] loginVerify = {"/**"};
    private String[] loginExcludeVerify = {"/other/**"};

    private String[] admin = {"/admin/**"};
    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(loginInterceptor())
                .addPathPatterns(loginVerify)
                .excludePathPatterns(loginExcludeVerify);

        interceptorRegistry.addInterceptor(adminInterceptor())
                .addPathPatterns(admin);
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
