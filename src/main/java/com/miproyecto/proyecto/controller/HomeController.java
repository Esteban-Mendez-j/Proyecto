package com.miproyecto.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/admin/home")
    public String getAdminHome() {
        return "home/admin";
    }

    @GetMapping("/candidato/home")
    public String getCandidatoHome() {
        return "home/candidato";
    }
    @GetMapping("/empresa/home")
    public String getEmpresaHome() {
        return "home/empresa";
    }

    @GetMapping("/")
    public String getInvitadoHome() {
        return "home/index";
    }
}
