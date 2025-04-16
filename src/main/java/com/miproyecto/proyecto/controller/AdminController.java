package com.miproyecto.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.miproyecto.proyecto.service.AdminService;
import com.miproyecto.proyecto.service.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/listUser/activos")
    public String ListUserActive(Model model) {
        model.addAttribute("ListUser", usuarioService.findAllByBannedStatus(true));
        model.addAttribute("userIsActive", true);
        return "usuario/list";
    }

    @GetMapping("/listUser/baneados")
    public String ListUserBan(Model model) {
        model.addAttribute("ListUser", usuarioService.findAllByBannedStatus(false));
        model.addAttribute("userIsActive", false);
        return "usuario/list";
    }


    @PostMapping("/agregarRol")
    public String NuevoAdmin (@RequestParam(name = "idUsuario") Long IdUsuario) {
        adminService.modificarRoles(IdUsuario, true);
        return "redirect:/admin/listUser/activos";
    }

    @PostMapping("/removerRol")
    public String quitarAdmin (@RequestParam(name = "idUsuario") Long IdUsuario) {
        adminService.modificarRoles(IdUsuario, false);
        return "redirect:/admin/listUser/activos";
    }


    @GetMapping("/banear/usuario")
    public String banearCuentas (Model model, @RequestParam(name = "idUsuario") Long IdUsuario){
        model.addAttribute("banear", true);
        model.addAttribute("idUsuario", IdUsuario);
        return "admin/ventanaBan";
    }

    @PostMapping("/banear/usuario")
    public String banearCuentas(
                    @RequestParam(name = "idUsuario") Long IdUsuario,
                    @RequestParam(name = "comentario") String comentario){
        adminService.cambiarIsActive(IdUsuario, false, comentario);
        return "redirect:/admin/listUser/activos";
    }

    @GetMapping("/desbanear/usuario")
    public String desbanearCuentas (Model model, @RequestParam(name = "idUsuario") Long IdUsuario){
        model.addAttribute("banear", false);
        model.addAttribute("idUsuario", IdUsuario);
        return "admin/ventanaBan";
    }

    @PostMapping("/desbanear/usuario")
    public String desbanearCuentas(
                    @RequestParam(name = "idUsuario") Long IdUsuario,
                    @RequestParam(name = "comentario") String comentario){
        adminService.cambiarIsActive(IdUsuario, true, comentario);
        return "redirect:/admin/listUser/baneados";
    }

    @GetMapping("/desactivar/vacantes")
    public String desactivarVacantes (Model model){
        model.addAttribute("activar", false);
        return "vacante/ventanaEstado";
    }

    //arreglar redireccion
    @PostMapping("/desactivar/vacantes")
    public String desactivarVacantes(
                    @RequestParam(name = "NVacante") Long NVacante,
                    @RequestParam(name = "comentario") String comentario){
        adminService.cambiarEstadoVacantes(NVacante, "desactivada", comentario);
        return "redirect:";
    }

    @GetMapping("/activar/vacantes")
    public String activarVacantes (Model model){
        model.addAttribute("activar", true);
        return "vacante/ventanaEstado";
    }

    @PostMapping("/activar/vacantes")
    public String activarVacantes(
                    @RequestParam(name = "NVacante") Long NVacante,
                    @RequestParam(name = "comentario") String comentario){
        adminService.cambiarEstadoVacantes(NVacante, "activada", comentario);
        return "redirect:";
    }
}
