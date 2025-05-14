package com.miproyecto.proyecto.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miproyecto.proyecto.util.JwtUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/* Redireccionamiento segun el Rol */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtils jwtUtils;

    public CustomAuthenticationSuccessHandler(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
            HttpServletResponse response, Authentication authentication)
            throws IOException {

        String jwtToken = jwtUtils.createToken(authentication);

        // Crear cookie JWT correctamente
        ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", jwtToken)
        .httpOnly(true)
        .secure(false) // true si usas HTTPS en producción
        .sameSite("lix") // O "Strict" según necesites
        .path("/")
        .maxAge(60*60)
        .build();
        // Agregar cookie a la respuesta
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        HttpSession session = request.getSession();
        session.setAttribute("jwtToken", jwtToken);

        // Obtener los roles de la autenticación
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        String rolPrincipal = decodedJWT.getClaim("rolPrincipal").asString();
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("roles", roles);
        responseBody.put("rolPrincipal", rolPrincipal);
        // Configurar tipo de contenido y devolver JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), responseBody);
    }

}



