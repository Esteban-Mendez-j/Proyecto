package com.miproyecto.proyecto.config;


import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class ImgConfig implements WebMvcConfigurer {

    public static final String UPLOAD_DIR = Paths.get("uploads", "img").toAbsolutePath().toString();

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + UPLOAD_DIR + "/");
    }
}


