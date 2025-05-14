package com.miproyecto.proyecto.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.FiltroVacanteDTO;
import com.miproyecto.proyecto.service.AdminService;
import com.miproyecto.proyecto.service.PostuladoService;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.service.VacanteService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin")  
public class AdminResource {
    
    @Autowired
    private PostuladoService postuladoService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private VacanteService vacanteService;
    @Autowired
    private JwtUtils jwtUtils ;


    @GetMapping("/listar/filtrados")
    public ResponseEntity<Map<String, Object>> listarUsuariosFiltrados(
            HttpSession session,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            // @RequestParam(name= "idUsuario", required = false) Long idUsuario,
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "rolPrinciapl", required = false) String rol,
            @RequestParam(name = "estado", required = false) Boolean estado) {

        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
        
        Map<String, Object> response = usuarioService.buscarUsuariosConFiltros(idUsuario, nombre, rol, estado, pageable);
        return ResponseEntity.ok(response);
    }




    // Obtener usuarios activos
    // @GetMapping("/listUser/activos")
    // public ResponseEntity<Map<String, Object>> getActiveUsers(HttpSession session, Pageable pageable) {
    //     String jwtToken = (String) session.getAttribute("jwtToken");
    //     DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
    //     Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
        
    //     List<UsuarioDTO> usuarios = usuarioService.findAllByBannedStatus(true, idUsuario, pageable);
    //     Map<String, Object> response = new HashMap<>();
    //     response.put("usuarios", usuarios);
    //     response.put("userIsActive", true);
    //     response.put("isSUPER_ADMIN", usuarioService.get(idUsuario).getRoles().contains("SUPER_ADMIN"));

    //     return ResponseEntity.ok(response);
    // }

    // // Obtener usuarios baneados
    // @GetMapping("/listUser/baneados")
    // public ResponseEntity<Map<String, Object>> getBannedUsers(HttpSession session) {
    //     String jwtToken = (String) session.getAttribute("jwtToken");
    //     DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
    //     Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

    //     List<UsuarioDTO> usuarios = usuarioService.findAllByBannedStatus(false, idUsuario);
    //     Map<String, Object> response = new HashMap<>();
    //     response.put("usuarios", usuarios);
    //     response.put("userIsActive", false);
    //     response.put("isSUPER_ADMIN", usuarioService.get(idUsuario).getRoles().contains("SUPER_ADMIN"));

    //     return ResponseEntity.ok(response);
    // }

    // Obtener vacantes activas
    @PostMapping("/listar/filtrovacantes")
    public ResponseEntity<Map<String, Object>> listarVacantes(
        HttpSession session, @PageableDefault(page = 0, size = 10) Pageable pageable,
        @RequestBody FiltroVacanteDTO filtro) {
        Map<String, Object> response = vacanteService.buscarVacantesConFiltros(filtro, pageable);
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
        postuladoService.cambiarEstadoPorUsuario(idUsuario, estado);
        adminService.cambiarIsActive(idUsuario, estado, comentario);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Estado de usuario actualizado");
        return ResponseEntity.ok(response);
    }

    // Cambiar estado de vacante
    @PostMapping("/cambiar-estado/vacantes")
    public ResponseEntity<Map<String, String>> changeVacancyStatus(
            @RequestParam("nvacante") Long nvacante,
            @RequestParam("estado") boolean estado,
            @RequestParam("comentario") String comentario) {

        if (estado == vacanteService.get(nvacante).isActive()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "La vacante ya está " + estado);
            System.out.println("Mensaje desde admin resource: "+ estado);
            System.out.println("Mensaje desde adninresource: "+ vacanteService.get(nvacante).isActive());
            return ResponseEntity.badRequest().body(errorResponse);
        }

        adminService.cambiarEstadoVacantes(nvacante, estado, comentario);
        postuladoService.cambiarEstadoVacantes(nvacante, estado);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Estado de vacante actualizado");
        return ResponseEntity.ok(response);
    }
}
