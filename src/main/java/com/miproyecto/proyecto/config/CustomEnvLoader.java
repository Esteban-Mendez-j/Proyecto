package com.miproyecto.proyecto.config;

import io.github.cdimascio.dotenv.Dotenv;


public class CustomEnvLoader {
    public static void load() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./") 
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        // // Variables de Base de datos
        // System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL", "jdbc:mysql://localhost:3306/mydb?serverTimezone=UTC"));
        // System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME", "root"));
        // System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD", ""));

        // Configuración de JPA
        System.setProperty("SPRING_JPA_HIBERNATE_DDL_AUTO", dotenv.get("SPRING_JPA_HIBERNATE_DDL_AUTO", "update"));

        // JWT / Seguridad
        System.setProperty("MY_SECRET_KEY", dotenv.get("MY_SECRET_KEY", "ClaveSecretaJWT"));
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION", "1800000"));

        // Rutas para imágenes y PDFs
        System.setProperty("UPLOAD_DIR_IMG", dotenv.get("UPLOAD_DIR_IMG", "uploads/img"));
        System.setProperty("UPLOAD_DIR_PDF", dotenv.get("UPLOAD_DIR_PDF", "uploads/pdf"));
    
    }
}



