package com.miproyecto.proyecto.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);            
        cookie.setSecure(true);              
        cookie.setPath("/");                 
        cookie.setMaxAge(60 * 60);           // 1 hora

        response.addCookie(cookie);
    
        HttpSession session = request.getSession();
        session.setAttribute("jwtToken", jwtToken);

        String redirectURL = request.getContextPath();

        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) 
                || authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            redirectURL = "http://localhost:4321/";
        } else if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EMPRESA"))) {
            redirectURL = "http://localhost:4321/empleos";
        }else{
            redirectURL = "http://localhost:4321/chat";
        }

        response.sendRedirect(redirectURL);

    }
}



