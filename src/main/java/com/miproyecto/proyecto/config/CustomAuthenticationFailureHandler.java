package com.miproyecto.proyecto.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        String correo = request.getParameter("username");
        UsuarioDTO usuario = usuarioService.findByCorreo(correo);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Map<String, Object> responseBody = new HashMap<>();
        if (usuario != null && !usuario.getIsActive()) {
            responseBody.put("status", "banned");
            responseBody.put("mensaje", "Tu cuenta esta desabilitada");
            responseBody.put("mensajeAdmin", usuario.getComentarioAdmin());
        } else {
            responseBody.put("status", "error");
            responseBody.put("mensaje", "Correo o contraseña incorrecta");
        }

        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);
    }

}
