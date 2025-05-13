package com.miproyecto.proyecto.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.EmpresaDTO;
import com.miproyecto.proyecto.service.EmpresaService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@RestController
@RequestMapping(value = "/api/empresas", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmpresaResource {

    private final EmpresaService empresaService;
    private final JwtUtils jwtUtils;

    

    public EmpresaResource(EmpresaService empresaService, JwtUtils jwtUtils) {
        this.empresaService = empresaService;
        this.jwtUtils = jwtUtils;
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

    @PutMapping("/edit/{idEmpresa}")
    public ResponseEntity<Long> updateEmpresa(
            @PathVariable(name = "idEmpresa") final Long idEmpresa,
            @RequestBody @Valid final EmpresaDTO empresaDTO) {
        empresaService.update(idEmpresa, empresaDTO);
        return ResponseEntity.ok(idEmpresa);
    }

    @DeleteMapping("/delete/{idEmpresa}")
    public ResponseEntity<Void> deleteEmpresa(
            @PathVariable(name = "idEmpresa") final Long idEmpresa) {
        
        empresaService.delete(idEmpresa);
        return ResponseEntity.noContent().build();
    }

    // @PostMapping("/buscar")
    // public ResponseEntity<List<EmpresaDTO>> buscarEmpresas(@RequestBody EmpresaDTO filtro) {
    //     List<EmpresaDTO> resultados = empresaService.buscarEmpresasConFiltro(filtro);
    //     return ResponseEntity.ok(resultados);
    // }
}

