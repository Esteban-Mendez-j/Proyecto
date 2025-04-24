package com.miproyecto.proyecto.rest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.CandidatoDTO;
import com.miproyecto.proyecto.service.CandidatoService;
import com.miproyecto.proyecto.service.EstudioService;
import com.miproyecto.proyecto.service.HistorialLaboralService;
import com.miproyecto.proyecto.service.PostuladoService;
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
@RequestMapping(value = "/api/candidatos", produces = MediaType.APPLICATION_JSON_VALUE)
public class CandidatoResource {

    private final CandidatoService candidatoService;
    private final JwtUtils jwtUtils;
    private final PostuladoService postuladoService;
    private final EstudioService estudioService;
    private final HistorialLaboralService historialLaboralService;

    public CandidatoResource(CandidatoService candidatoService, JwtUtils jwtUtils, PostuladoService postuladoService,
            EstudioService estudioService, HistorialLaboralService historialLaboralService) {
        this.candidatoService = candidatoService;
        this.jwtUtils = jwtUtils;
        this.postuladoService = postuladoService;
        this.estudioService = estudioService;
        this.historialLaboralService = historialLaboralService;
    }

    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> mostrarPerfil( 
            @RequestParam(name = "idUsuario", required = false) Long idUsuario,
            @RequestParam(name = "nPostulacion", required = false) Long nPostulacion,
            Model model, HttpSession session) {

        Map<String, Object> response = new HashMap<>();        
        if (idUsuario == null && nPostulacion == null) {
            // Sacamos el ID del usuario que inicia sesion
            String jwtToken = (String) session.getAttribute("jwtToken");
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
        } else{

            if (postuladoService.get(nPostulacion).getCandidato().getId() != idUsuario && nPostulacion != null) {
                response.put("error", "No tienes Permiso para acceder");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
        }
        
        CandidatoDTO candidatoDTO = candidatoService.get(idUsuario);
        if(candidatoDTO == null){return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);}
        
        response.put("estudios", estudioService.getEstudiosByIdUsuario(idUsuario));
        response.put("historialLaboral", historialLaboralService.getHistorialByIdUsuario(idUsuario));
        response.put("candidato", candidatoDTO);
        return ResponseEntity.ok(response); 
    }

    @GetMapping("/{idCandidato}")
    public ResponseEntity<CandidatoDTO> getCandidato(
            @PathVariable(name = "idCandidato") final Long idCandidato) {
        return ResponseEntity.ok(candidatoService.get(idCandidato));
    }

    @PostMapping("/add")
    public ResponseEntity<Long> createCandidato(
            @RequestBody @Valid final CandidatoDTO candidatoDTO) {
        final Long createdIdCandidato = candidatoService.create(candidatoDTO);
        return new ResponseEntity<>(createdIdCandidato, HttpStatus.CREATED);
    }

    @PutMapping("/edit/{idUsuario}")
    public ResponseEntity<Long> updateCandidato(
            @PathVariable(name = "idCandidato") final Long idCandidato,
            @RequestBody @Valid final CandidatoDTO candidatoDTO) {
        candidatoService.update(idCandidato, candidatoDTO);
        return ResponseEntity.ok(idCandidato);
    }

    @DeleteMapping("/delete/{idCandidato}")
    public ResponseEntity<Void> deleteCandidato(
            @PathVariable(name = "idCandidato") final Long idCandidato) {
        candidatoService.delete(idCandidato);
        return ResponseEntity.noContent().build();
    }

}
