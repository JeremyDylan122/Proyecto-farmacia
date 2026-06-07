package com.msclientebeneficio.demo.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/jacoco/**")
                .addResourceLocations("file:///C:/Users/Jeremy/Desktop/Proyecto/MSclienteBeneficio/target/site/jacoco/");
    }
}
