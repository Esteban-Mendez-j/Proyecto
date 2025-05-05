package com.miproyecto.proyecto.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import com.auth0.jwt.exceptions.JWTVerificationException;


@RestControllerAdvice // nota la diferencia con @ControllerAdvice
public class GlobalException {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "No se encontró ningún valor");
    }

    @ExceptionHandler(Forbidden.class)
    public ResponseEntity<?> handleForbidden(Forbidden ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "No tienes permiso para acceder");
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<?> handleJWT(JWTVerificationException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "El token es inválido");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    // @ExceptionHandler(TokenExpiredException.class)
    // public String handleExpiredTokenException() {
    //     return "redirect:/?expired=1"; 
    // }

    private ResponseEntity<?> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
    
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errores.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errores);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}
