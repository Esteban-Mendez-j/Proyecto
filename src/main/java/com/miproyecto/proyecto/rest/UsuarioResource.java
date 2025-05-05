package com.miproyecto.proyecto.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping(value = "/api/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioResource {

    private final UsuarioService usuarioService;

    public UsuarioResource(final UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login/error")
    public ResponseEntity<Map<String, Object>> loginError(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        String correo = (String) request.getSession().getAttribute("LOGIN_EMAIL");
        String mensajeError = (String) request.getSession().getAttribute("LOGIN_ERROR_MESSAGE");
        Boolean isBanned = (Boolean) request.getSession().getAttribute("IS_BANNED");

        if (isBanned != null && isBanned) {
            response.put("status", "banned");
            response.put("correo", correo);
            response.put("mensaje", mensajeError);
        } else {
            response.put("status", "error");
            response.put("mensaje", WebUtils.getMessage(mensajeError));
        }

        // Limpiar los atributos de sesión
        request.getSession().removeAttribute("LOGIN_EMAIL");
        request.getSession().removeAttribute("LOGIN_ERROR_MESSAGE");
        request.getSession().removeAttribute("IS_BANNED");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    

    @PostMapping("/add")
    public ResponseEntity<Long> createUsuario(@RequestBody @Valid final UsuarioDTO usuarioDTO) {
        final Long createdIdUsuario = usuarioService.create(usuarioDTO);
        return new ResponseEntity<>(createdIdUsuario, HttpStatus.CREATED);
    }

    @GetMapping("/edit/{idUsuario}")
    public ResponseEntity<UsuarioDTO> getUsuario(
            @PathVariable(name = "idUsuario") final Long idUsuario) {
        return ResponseEntity.ok(usuarioService.get(idUsuario));
    }
    
    @PutMapping("/edit/{idUsuario}")
    public ResponseEntity<Long> updateUsuario(
            @PathVariable(name = "idUsuario") final Long idUsuario,
            @RequestBody @Valid final UsuarioDTO usuarioDTO) {
        usuarioService.update(idUsuario, usuarioDTO);
        return ResponseEntity.ok(idUsuario);
    }

    @DeleteMapping("/delete/{idUsuario}")
    public ResponseEntity<Void> deleteUsuario(
            @PathVariable(name = "idUsuario") final Long idUsuario) {
        usuarioService.delete(idUsuario);
        return ResponseEntity.noContent().build();
    }
    // @GetMapping("/listar")
    // public ResponseEntity<Map<String, Object>> listarUsuarios(
    //     HttpSession session, @PageableDefault(page = 0, size = 10) Pageable pageable) {

    //     Map<String, Object> response = usuarioService.findAllByEstado(true, pageable, "usuarios");
    //     return ResponseEntity.ok(response);
    // }



}