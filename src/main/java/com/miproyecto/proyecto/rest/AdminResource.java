package com.miproyecto.proyecto.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.service.AdminService;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.service.VacanteService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin")
public class AdminResource {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private VacanteService vacanteService;
    @Autowired
    private JwtUtils jwtUtils;

    // Obtener usuarios activos
    @GetMapping("/listUser/activos")
    public ResponseEntity<Map<String, Object>> getActiveUsers(HttpSession session) {
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
        
        List<UsuarioDTO> usuarios = usuarioService.findAllByBannedStatus(true, idUsuario);
        Map<String, Object> response = new HashMap<>();
        response.put("usuarios", usuarios);
        response.put("userIsActive", true);
        response.put("isSUPER_ADMIN", usuarioService.get(idUsuario).getRoles().contains("SUPER_ADMIN"));

        return ResponseEntity.ok(response);
    }

    // Obtener usuarios baneados
    @GetMapping("/listUser/baneados")
    public ResponseEntity<Map<String, Object>> getBannedUsers(HttpSession session) {
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        List<UsuarioDTO> usuarios = usuarioService.findAllByBannedStatus(false, idUsuario);
        Map<String, Object> response = new HashMap<>();
        response.put("usuarios", usuarios);
        response.put("userIsActive", false);
        response.put("isSUPER_ADMIN", usuarioService.get(idUsuario).getRoles().contains("SUPER_ADMIN"));

        return ResponseEntity.ok(response);
    }

    // Obtener vacantes activas
    @GetMapping("/listVacantes/activas")
    public ResponseEntity<Map<String, Object>> getActiveVacancies(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        Map<String, Object> response = vacanteService
            .findAllByEstado(true, pageable, "vacantes");
        return ResponseEntity.ok(response);
    }

    // Obtener vacantes desactivadas
    @GetMapping("/listVacantes/desactivadas")
    public ResponseEntity<Map<String, Object>> getInactiveVacancies(@PageableDefault(page = 0, size = 10) Pageable pageable) {
        Map<String, Object> response = vacanteService
            .findAllByEstado(false, pageable, "vacantesDesactivadas");
        return ResponseEntity.ok(response);
    }

    // Agregar rol de administrador a un usuario
    @PostMapping("/agregarRol")
    public ResponseEntity<Map<String, String>> addAdminRole(@RequestParam("idUsuario") Long idUsuario) {
        adminService.modificarRoles(idUsuario, true);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Rol de admin agregado exitosamente");
        return ResponseEntity.ok(response);
    }

    // Eliminar rol de administrador de un usuario
    @PostMapping("/removerRol")
    public ResponseEntity<Map<String, String>> removeAdminRole(@RequestParam("idUsuario") Long idUsuario) {
        adminService.modificarRoles(idUsuario, false);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Rol de admin removido exitosamente");
        return ResponseEntity.ok(response);
    }

    // Cambiar estado de un usuario
    @PostMapping("/cambiar-estado/usuario")
    public ResponseEntity<Map<String, String>> changeUserStatus(
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("estado") boolean estado,
            @RequestParam("comentario") String comentario) {

        if (estado == usuarioService.get(idUsuario).getIsActive()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "El usuario ya se encuentra en ese estado");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        adminService.cambiarIsActive(idUsuario, estado, comentario);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Estado de usuario actualizado");
        return ResponseEntity.ok(response);
    }

    // Cambiar estado de vacante
    @PostMapping("/cambiar-estado/vacantes")
    public ResponseEntity<Map<String, String>> changeVacancyStatus(
            @RequestParam("NVacante") Long nVacante,
            @RequestParam("estado") boolean estado,
            @RequestParam("comentario") String comentario) {

        if (estado == vacanteService.get(nVacante).isActive()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "La vacante ya está " + estado);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        adminService.cambiarEstadoVacantes(nVacante, estado, comentario);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Estado de vacante actualizado");
        return ResponseEntity.ok(response);
    }
}
