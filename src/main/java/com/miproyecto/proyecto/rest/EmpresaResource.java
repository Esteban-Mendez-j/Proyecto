package com.miproyecto.proyecto.rest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.EmpresaDTO;
import com.miproyecto.proyecto.service.EmpresaService;
import com.miproyecto.proyecto.service.VacanteService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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


@RestController
@RequestMapping(value = "/api/empresas", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmpresaResource {

    private final EmpresaService empresaService;
    private final JwtUtils jwtUtils;
    private final VacanteService vacanteService;

    

    public EmpresaResource(EmpresaService empresaService, JwtUtils jwtUtils, VacanteService vacanteService) {
        this.empresaService = empresaService;
        this.jwtUtils = jwtUtils;
        this.vacanteService = vacanteService;
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
        response.put("vacantes", vacanteService.findByIdUsuario(idUsuario));
        response.put("empresa", empresaDTO);
        return ResponseEntity.ok(response); 
    }

    @PostMapping("/add")
    public ResponseEntity<Long> createEmpresa(@RequestBody @Valid final EmpresaDTO empresaDTO) {
        final Long createdIdEmpresa = empresaService.create(empresaDTO);
        return new ResponseEntity<>(createdIdEmpresa, HttpStatus.CREATED);
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

}
