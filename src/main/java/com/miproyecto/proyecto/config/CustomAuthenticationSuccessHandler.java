package com.miproyecto.proyecto.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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

        // Guardar en cookie si lo deseas (opcional)
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); 
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 1 hora
        response.addCookie(cookie);


        response.addHeader("Set-Cookie", String.format(
        "jwtToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None",
        jwtToken, 60 * 60));

        HttpSession session = request.getSession();
        session.setAttribute("jwtToken", jwtToken);

        // Obtener los roles de la autenticación
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        List<String> priority = List.of("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_EMPRESA", "ROLE_CANDIDATO");

        String rolPrincipal = priority.stream()
            .filter(roles::contains)
            .findFirst()
            .orElse(roles.get(0));
        
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



