package com.miproyecto.proyecto.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.service.UsuarioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String correo = request.getParameter("username");
        request.getSession().setAttribute("LOGIN_EMAIL", correo);

        String errorMessage = "Correo o contraseña incorrecta";
        boolean isBanned = false; 
        
        UsuarioDTO usuario = usuarioService.findByCorreo(correo);
        if (usuario != null && !usuario.getIsActive()) {
            errorMessage =  usuario.getComentarioAdmin();
            isBanned = true;

        }

        request.getSession().setAttribute("IS_BANNED", isBanned);
        request.getSession().setAttribute("LOGIN_ERROR_MESSAGE", errorMessage);
        response.sendRedirect("/usuarios/login/error");
        
    }
}
