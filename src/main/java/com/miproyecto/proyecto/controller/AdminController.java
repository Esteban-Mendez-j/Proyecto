package com.miproyecto.proyecto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.VacanteDTO;
import com.miproyecto.proyecto.service.AdminService;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.service.VacanteService;
import com.miproyecto.proyecto.util.JwtUtils;
import com.miproyecto.proyecto.util.WebUtils;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private VacanteService vacanteService;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/listUser/activos")
    public String ListUserActive(Model model, HttpSession session) {
        // Sacamos el ID del usuario que inicia sesion
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
        model.addAttribute("ListUser", usuarioService.findAllByBannedStatus(true, idUsuario));
        model.addAttribute("userIsActive", true);
        model.addAttribute("isSUPER_ADMIN", usuarioService.get(idUsuario).getRoles().contains("SUPER_ADMIN"));
        return "usuario/list";
    }

    @GetMapping("/listUser/baneados")
    public String ListUserBan(Model model, HttpSession session) {
        // Sacamos el ID del usuario que inicia sesion
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
        model.addAttribute("ListUser", usuarioService.findAllByBannedStatus(false, idUsuario));
        model.addAttribute("userIsActive", false);
        model.addAttribute("isSUPER_ADMIN", usuarioService.get(idUsuario).getRoles().contains("SUPER_ADMIN"));
        return "usuario/list";
    }

    @GetMapping("/listVacantes/activas")
    public String ListVacanteActive(Model model) {
        return "redirect:/vacantes/listar";
    }

    @GetMapping("/listVacantes/desactivadas")
    public String ListVacanteDesactive(Model model, @ModelAttribute VacanteDTO filtro) {
        List<VacanteDTO> vacantesDesactivadas = vacanteService.findAllByEstado("desactivada"); 
        VacanteDTO vacanteSeleccion = null;
        if(!vacantesDesactivadas.isEmpty()){
            Long nVacanteSeleccion = vacantesDesactivadas.get(0).getNvacantes();
            vacanteSeleccion = vacanteService.get(nVacanteSeleccion);
        }
        model.addAttribute("vacanteSeleccionada", vacanteSeleccion);
        model.addAttribute("vacantes", vacantesDesactivadas);
        model.addAttribute("filtro", filtro);

        return "html/ofertas";
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

    @GetMapping("/cambiar-estado/usuario")
    public String cambiarEstadoUsuario(Model model,@RequestParam(name = "idUsuario") Long idUsuario){
        model.addAttribute("isActive", usuarioService.get(idUsuario).getIsActive());
        model.addAttribute("idUsuario", idUsuario);
        return "admin/ventanaBan"; // Vista compartida para ambos casos
    }

    @PostMapping("/cambiar-estado/usuario")
    public String cambiarEstadoUsuario(
                        @RequestParam(name = "idUsuario") Long idUsuario,
                        @RequestParam(name = "estado") boolean estado,
                        @RequestParam(name = "comentario") String comentario,
                        RedirectAttributes redirectAttributes) {
        
        if (estado == usuarioService.get(idUsuario).getIsActive()) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR , WebUtils.getMessage("El usuario ya se encuentra en ese estado"));
            redirectAttributes.addAttribute("idUsuario", idUsuario);
            return "redirect:/admin/cambiar-estado/usuario";
        }      
        adminService.cambiarIsActive(idUsuario, estado, comentario);

        if (estado) {
            return "redirect:/admin/listUser/baneados";
        } else {
            return "redirect:/admin/listUser/activos";
        }
    }


    @GetMapping("/cambiar-estado/vacantes")
    public String cambiarEstadoVacante(Model model, @RequestParam("NVacante") Long NVacante) {
        model.addAttribute("NVacante", NVacante);
        model.addAttribute("estadoActual", vacanteService.get(NVacante).getEstado());
        return "vacante/ventanaEstado";
    }
    
    @PostMapping("/cambiar-estado/vacantes")
    public String cambiarEstadoVacante(@RequestParam("NVacante") Long NVacante,
                                       @RequestParam("estado") String estado,
                                       @RequestParam("comentario") String comentario,
                                       RedirectAttributes redirectAttributes) {

        if (estado.equalsIgnoreCase(vacanteService.get(NVacante).getEstado())) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR , WebUtils.getMessage("La vacante ya esta "+ estado));
            redirectAttributes.addAttribute("NVacante", NVacante);
            return "redirect:/admin/cambiar-estado/vacantes";
        }

        adminService.cambiarEstadoVacantes(NVacante, estado, comentario);

        if ("activa".equalsIgnoreCase(estado)) {
            return "redirect:/admin/listVacantes/desactivadas";
        } else {
            return "redirect:/admin/listVacantes/activas";
        }
    }
    
}
