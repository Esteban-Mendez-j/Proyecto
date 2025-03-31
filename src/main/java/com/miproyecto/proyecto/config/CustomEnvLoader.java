package com.miproyecto.proyecto.config;

import io.github.cdimascio.dotenv.Dotenv;

public class CustomEnvLoader {
    public static void load() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./") 
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        System.setProperty("MY_SECRET_KEY", dotenv.get("MY_SECRET_KEY", "default_value"));
        System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION", "1800000"));
    }
}
