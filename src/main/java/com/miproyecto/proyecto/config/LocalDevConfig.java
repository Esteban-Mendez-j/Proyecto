package com.miproyecto.proyecto.config;

import java.io.File;
import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;


/**
 * Cargue archivos Thymeleaf desde el sistema de archivos durante el desarrollo, 
 * sin ningún almacenamiento en caché.
 * 
 * Nos permite ver los cambios de los archivos HTML sin reiniciar la app 
 * (Recomendado solo para el desarrollo)
 */
@Configuration
@Profile("local")
public class LocalDevConfig {

    public LocalDevConfig(final TemplateEngine templateEngine) throws IOException {
        final ClassPathResource applicationYml = new ClassPathResource("application.yml");
        if (applicationYml.isFile()) {
            File sourceRoot = applicationYml.getFile().getParentFile();
            while (sourceRoot.listFiles((dir, name) -> name.equals("mvnw")).length != 1) {
                sourceRoot = sourceRoot.getParentFile();
            }
            final FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
            fileTemplateResolver.setPrefix(sourceRoot.getPath() + "/src/main/resources/templates/");
            fileTemplateResolver.setSuffix(".html");
            fileTemplateResolver.setCacheable(false);
            fileTemplateResolver.setCharacterEncoding("UTF-8");
            fileTemplateResolver.setCheckExistence(true);
            templateEngine.setTemplateResolver(fileTemplateResolver);
        }
    }

}
