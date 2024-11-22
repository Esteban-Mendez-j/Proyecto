package com.miproyecto.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("login") == null) {
            session.setAttribute("login", null); 
            session.setAttribute("tipo", "invitado");
            session.setAttribute("imagen", null);
        }
        return "home/index";
    }

}
