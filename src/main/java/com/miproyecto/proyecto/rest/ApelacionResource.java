package com.miproyecto.proyecto.rest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.ApelacionDTO;
import com.miproyecto.proyecto.service.ApelacionService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@RestController
@RequestMapping(value = "/api/apelacion", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApelacionResource {

    private final ApelacionService apelacionService;
    private final JwtUtils jwtUtils;

    public ApelacionResource(ApelacionService apelacionService, JwtUtils jwtUtils) {
        this.apelacionService = apelacionService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/admin/list")
    public ResponseEntity<List<ApelacionDTO>> getAllApelacions() {
        return ResponseEntity.ok(apelacionService.findAll());
    }

    @GetMapping("/list")
    public ResponseEntity<List<ApelacionDTO>> list(Model model, HttpSession session) {
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        List<ApelacionDTO> apelaciones =  apelacionService.findByUsuarioId(idUsuario);
        return ResponseEntity.ok(apelaciones) ; 
    }


    @GetMapping("/cambiarEstado/{idApelacion}")
    public ResponseEntity<ApelacionDTO> cambiarEstado(@PathVariable("idApelacion") final Long idApelacion, Model model) {
        ApelacionDTO apelacionDTO = apelacionService.get(idApelacion);

        if (apelacionDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si no existe la apelación, redirigimos
        }
        return ResponseEntity.ok(apelacionDTO); // Vista para cambiar el estado de la apelación
    }

    @PostMapping("/cambiarEstado/{idApelacion}")
    public ResponseEntity<Long> cambiarEstado(@PathVariable("idApelacion") final Long idApelacion,
            @RequestParam("estado") final String estado, RedirectAttributes redirectAttributes) {

        ApelacionDTO apelacionDTO = apelacionService.get(idApelacion);

        if (apelacionDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si no existe la apelación, redirigimos
        }
        // Cambiamos el estado de la apelación
        apelacionDTO.setEstado(estado);
        apelacionService.update(idApelacion, apelacionDTO);
        return ResponseEntity.ok(idApelacion); // Redirigimos a la lista de apelaciones
    }

    @PostMapping("/add")
    public ResponseEntity<Long> createApelacion(
            @RequestBody @Valid final ApelacionDTO apelacionDTO) {
        final Long createdId = apelacionService.create(apelacionDTO);;
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<ApelacionDTO> getApelacion(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(apelacionService.get(id));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Long> updateApelacion(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ApelacionDTO apelacionDTO) {
        apelacionService.update(id, apelacionDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteApelacion(@PathVariable(name = "id") final Long id) {
        apelacionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
