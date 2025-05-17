package com.miproyecto.proyecto.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@RestController
@RequestMapping(value = "/api/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsuarioResource {

    private final UsuarioService usuarioService;
    private final JwtUtils jwtUtils;


    
    public UsuarioResource(UsuarioService usuarioService, JwtUtils jwtUtils) {
        this.usuarioService = usuarioService;
        this.jwtUtils = jwtUtils;
    }


    @GetMapping("/rol")
    public ResponseEntity<Map<String, Object>> getRol( @CookieValue(name = "jwtToken", required = false) String jwtToken, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
      
        if (jwtToken == null) {
            // Si no hay token en la sesión, devolver rol de invitado
            response.put("rolPrincipal", "ROLE_INVITADO");
            response.put("roles", List.of("ROLE_INVITADO"));
            return ResponseEntity.ok(response);
        }
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        if (decodedJWT == null) {
            // Si el token es inválido, también devolver como invitado
            response.put("rolPrincipal", "ROLE_INVITADO");
            response.put("roles", List.of("ROLE_INVITADO"));
            return ResponseEntity.ok(response);
        }

        // Token válido: extraer claims
        List<String> roles = decodedJWT.getClaim("authorities").asList(String.class);
        String rolPrincipal = decodedJWT.getClaim("rolPrincipal").asString();

        response.put("rolPrincipal", rolPrincipal);
        response.put("id", Long.parseLong(jwtUtils.extractUsername(decodedJWT)));
        response.put("roles", roles);
        return ResponseEntity.ok(response);
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