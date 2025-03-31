package com.miproyecto.proyecto.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;


@Configuration
public class ImgConfig implements WebMvcConfigurer {

    public static final String UPLOAD_DIR = Path.of("uploads", "img").toAbsolutePath().toString();

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + UPLOAD_DIR + "/");
    }
}


