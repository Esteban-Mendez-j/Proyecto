package com.miproyecto.proyecto.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class ArchivoConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir.img}")
    private String imgUploadDir;

    @Value("${app.upload-dir.pdf}")
    private String pdfUploadDir;

    @Override
    public void addResourceHandlers( @NonNull ResourceHandlerRegistry registry) {
        // Servir imágenes desde el directorio 'uploads/img'
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:" + imgUploadDir + "/");

        // Servir PDFs desde el directorio 'uploads/pdf'
        registry.addResourceHandler("/pdf/**")
                .addResourceLocations("file:" + pdfUploadDir + "/");
    }
}
