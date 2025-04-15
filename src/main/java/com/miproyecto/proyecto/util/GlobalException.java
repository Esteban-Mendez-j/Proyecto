package com.miproyecto.proyecto.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException.Forbidden;
import org.springframework.web.servlet.ModelAndView;

import com.auth0.jwt.exceptions.JWTVerificationException;


@ControllerAdvice
public class GlobalException {

    public ModelAndView ExceptionContent(HttpStatus status, String mensaje) {
        ModelAndView modelAndView = new ModelAndView("exception"); 
        modelAndView.addObject("status", status.value()); 
        modelAndView.addObject("errorMessage", mensaje); 
        return modelAndView;
    }

    @ExceptionHandler(Forbidden.class)
    public ModelAndView autorizedException(Forbidden ex){
        return ExceptionContent(HttpStatus.FORBIDDEN, "No tienes Permiso para acceder");
    }
     
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView notFoundException (){
        return ExceptionContent(HttpStatus.NOT_FOUND, "No se encontro ningun valor");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView illegalArgumentException (){
        return ExceptionContent(HttpStatus.BAD_REQUEST, "Error al ingresar un dato ");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView serverError(){
        return ExceptionContent(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error en el servidor. Por favor, contacte a Soporte Tecnico.");
    }

    // @ExceptionHandler(TokenExpiredException.class)
    // public String handleExpiredTokenException() {
    //     return "redirect:/?expired=1"; 
    // }

    @ExceptionHandler(JWTVerificationException.class)
    public ModelAndView JWTVerificationException (){
        return ExceptionContent(HttpStatus.UNAUTHORIZED, "El token es invalido");
    }


  
}
