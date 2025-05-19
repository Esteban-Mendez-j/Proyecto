package com.miproyecto.proyecto.rest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.HistorialLaboralDTO;
import com.miproyecto.proyecto.service.HistorialLaboralService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
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


@RestController
@RequestMapping(value = "/api/historialLaborals", produces = MediaType.APPLICATION_JSON_VALUE)
public class HistorialLaboralResource {

    private final HistorialLaboralService historialLaboralService;
    private final JwtUtils jwtUtils;


    public HistorialLaboralResource(HistorialLaboralService historialLaboralService, JwtUtils jwtUtils) {
        this.historialLaboralService = historialLaboralService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<List<HistorialLaboralDTO>> getAllHistorialLaborals() {
        return ResponseEntity.ok(historialLaboralService.findAll());
    }

    @PostMapping("/add")
    public ResponseEntity<Long> createHistorialLaboral(
            @RequestBody @Valid final HistorialLaboralDTO historialLaboralDTO,
            HttpSession session) {
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
        historialLaboralDTO.setIdUsuario(idUsuario);
        final Long createdIDHistorial = historialLaboralService.create(historialLaboralDTO);
        return new ResponseEntity<>(createdIDHistorial, HttpStatus.CREATED);
    }

    @GetMapping("/edit/{iDHistorial}")
    public ResponseEntity<HistorialLaboralDTO> getHistorialLaboral(
            @PathVariable(name = "iDHistorial") final Long iDHistorial) {
        return ResponseEntity.ok(historialLaboralService.get(iDHistorial));
    }

    @PutMapping("/replace/{candidatoId}")
    public ResponseEntity<Void> replaceEstudios(
            @PathVariable Long candidatoId,
            @RequestBody List<HistorialLaboralDTO> historial) {
                
        System.out.println(candidatoId);
        System.out.println(historial);
        historialLaboralService.replaceHistorial(candidatoId, historial);
        return ResponseEntity.ok().build();   
    }

    @PutMapping("/edit/{iDHistorial}")
    public ResponseEntity<Long> updateHistorialLaboral(
            @PathVariable(name = "iDHistorial") final Long iDHistorial,
            @RequestBody @Valid final HistorialLaboralDTO historialLaboralDTO) {
        historialLaboralService.update(iDHistorial, historialLaboralDTO);
        return ResponseEntity.ok(iDHistorial);
    }

    @DeleteMapping("/delete/{iDHistorial}")
    public ResponseEntity<Void> deleteHistorialLaboral(
            @PathVariable(name = "iDHistorial") final Long iDHistorial) {
        historialLaboralService.delete(iDHistorial);
        return ResponseEntity.noContent().build();
    }

}
