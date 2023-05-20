package com.power.travel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {
//    请求本地服务器图片地址
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String file_path = "file:D:\\javaText\\";  //本地测试配置
        registry.addResourceHandler("/image/**")  //请求路径
                .addResourceLocations(file_path);        //本地地址

    }
}
