package com.miproyecto.proyecto.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.miproyecto.proyecto.util.JwtUtils;

import java.io.IOException;

/* Redireccionamiento segun el Rol */

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtils jwtUtils;

    public CustomAuthenticationSuccessHandler(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String jwtToken = jwtUtils.createToken(authentication);

        HttpSession session = request.getSession();
        session.setAttribute("jwtToken", jwtToken);

        String redirectURL = request.getContextPath();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectURL = "/admin/home";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA"))) {
            redirectURL = "/empresa/home";
        }else{
            redirectURL = "/candidato/home";
        }

        response.sendRedirect(redirectURL);
    }
}



