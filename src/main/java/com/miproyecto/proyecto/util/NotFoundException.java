package com.miproyecto.proyecto.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para indicar que un recurso no fue encontrado.
 * 
 * Al lanzar esta excepción en un controlador de Spring Boot, se devuelve 
 * automáticamente una respuesta con el estado HTTP 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super();
    }

    public NotFoundException(final String message) {
        super(message);
    }

}
