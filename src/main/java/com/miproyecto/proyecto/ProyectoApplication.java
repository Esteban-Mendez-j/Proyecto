package com.miproyecto.proyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.miproyecto.proyecto.config.CustomEnvLoader;


@SpringBootApplication
public class ProyectoApplication {
    public static void main(final String[] args) {
        CustomEnvLoader.load();// carga las variables de entorno
        SpringApplication.run(ProyectoApplication.class, args);
    }

}
