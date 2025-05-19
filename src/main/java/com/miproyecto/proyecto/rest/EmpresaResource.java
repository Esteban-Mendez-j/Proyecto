package com.miproyecto.proyecto.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.EmpresaDTO;
import com.miproyecto.proyecto.model.ValidationGroups;
import com.miproyecto.proyecto.service.EmpresaService;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;


@RestController
@RequestMapping(value = "/api/empresas", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmpresaResource {

    private final EmpresaService empresaService;
    private final JwtUtils jwtUtils;
    private UsuarioService usuarioService;


    public EmpresaResource(EmpresaService empresaService, JwtUtils jwtUtils, UsuarioService usuarioService) {
        this.empresaService = empresaService;
        this.jwtUtils = jwtUtils;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> mostrarPerfil( Model model,HttpSession session,
            @RequestParam(value = "idUsuario", required = false) Long idUsuario) {        
        
        Map<String, Object> response = new HashMap<>();     
        if (idUsuario == null) {
            // Sacamos el ID del usuario que inicia sesion
            String jwtToken = (String) session.getAttribute("jwtToken");
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
        } 
        EmpresaDTO empresaDTO = empresaService.get(idUsuario);        
        if (empresaDTO == null) {return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);} 
        response.put("empresa", empresaDTO);
        return ResponseEntity.ok(response); 
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createEmpresa(@RequestBody @Valid final EmpresaDTO empresaDTO) {
        Map<String, Object> response = new HashMap<>();
        empresaService.create(empresaDTO);
        response.put("status", HttpStatus.CREATED.value());
        response.put("mensaje", "Empresa creada con exito!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit/{idEmpresa}")
    public ResponseEntity<EmpresaDTO> getEmpresa(
            @PathVariable(name = "idEmpresa") final Long idEmpresa) {
        return ResponseEntity.ok(empresaService.get(idEmpresa));
    }



    @PutMapping(value = "/edit/{idUsuario}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> editCandidato(
            @RequestPart("empresa") @Validated({ValidationGroups.OnUpdate.class, Default.class}) EmpresaDTO empresaDTO,
            @RequestPart(name = "img", required = false) MultipartFile imagen,
            @CookieValue(name = "jwtToken", required = false) String jwtToken) {

        Map<String, Object> response = new HashMap<>();
        
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
 

        try {
            
            if (imagen != null && !imagen.isEmpty()) {
                if (empresaDTO.getImagen() != null && !empresaDTO.getImagen().isEmpty()) {

                    usuarioService.eliminarArchivo(empresaDTO.getImagen(), true);
                }
                String rutaImagen = usuarioService.guardarArchivo(imagen, idUsuario);
                empresaDTO.setImagen(rutaImagen);
            }

            // Actualizar los datos
            empresaService.update(idUsuario, empresaDTO);
            response.put("status", HttpStatus.OK.value());
            response.put("mensaje", "Candidato actualizado correctamente.");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("mensaje", "Error al guardar la imagen.");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("mensaje", "Error al actualizar el candidato.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/delete/{idEmpresa}")
    public ResponseEntity<Void> deleteEmpresa(
            @PathVariable(name = "idEmpresa") final Long idEmpresa) {
        
        empresaService.delete(idEmpresa);
        return ResponseEntity.noContent().build();
    }

}

