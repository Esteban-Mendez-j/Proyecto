package com.miproyecto.proyecto.rest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.VacanteDTO;
import com.miproyecto.proyecto.service.VacanteService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/vacantes", produces = MediaType.APPLICATION_JSON_VALUE)
public class VacanteResource {

    private final VacanteService vacanteService;
    private final JwtUtils jwtUtils;

    
   
    public VacanteResource(VacanteService vacanteService, JwtUtils jwtUtils) {
        this.vacanteService = vacanteService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<List<VacanteDTO>> list(HttpSession session) {
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        List<VacanteDTO> vacantes = vacanteService.findByIdUsuario(idUsuario);
        return ResponseEntity.ok(vacantes);
    }


    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listarVacantes(
            @ModelAttribute VacanteDTO filtro,
            HttpSession session) {

        session.setAttribute("filtro", filtro);

        List<VacanteDTO> vacantes;
        if (filtro != null && (
            (filtro.getCargo() != null && !filtro.getCargo().isEmpty()) ||
            (filtro.getCiudad() != null && !filtro.getCiudad().isEmpty()) ||
            (filtro.getTipo() != null && !filtro.getTipo().isEmpty()) ||
            (filtro.getModalidad() != null && !filtro.getModalidad().isEmpty()) ||
            (filtro.getTitulo() != null && !filtro.getTitulo().isEmpty())
        )) {
            vacantes = vacanteService.buscarVacantesConFiltros(filtro);
        } else {
            vacantes = vacanteService.findAllByEstado("activa");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("vacantes", vacantes);
        response.put("filtro", filtro);

        if (!vacantes.isEmpty()) {
            response.put("vacanteSeleccionada", vacanteService.get(vacantes.get(0).getNvacantes()));
        }

        return ResponseEntity.ok(response);
    }

   
    @GetMapping("/seleccion/{nvacantes}")
    public ResponseEntity<Map<String, Object>> seleccionVacante(
            @PathVariable(name = "nvacantes") Long nvacantes,
            HttpSession session) {

        VacanteDTO vacanteSeleccionada = vacanteService.get(nvacantes);

        if (vacanteSeleccionada == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Vacante no encontrada"));
        }

        List<VacanteDTO> vacantes = vacanteService.findAllByEstado("activa");
        VacanteDTO filtro = (VacanteDTO) session.getAttribute("filtro");

        Map<String, Object> response = new HashMap<>();
        response.put("vacantes", vacantes);
        response.put("filtro", filtro);
        response.put("vacanteSeleccionada", vacanteSeleccionada);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/eliminar/filtro")
    public ResponseEntity<Map<String, String>> eliminaFiltro(HttpSession session) {
        session.removeAttribute("filtro");
        return ResponseEntity.ok(Map.of("mensaje", "Filtro eliminado"));
    }
    

    @PostMapping("/add")
    public ResponseEntity<Long> createVacante(@RequestBody @Valid final VacanteDTO vacanteDTO) {
        final Long createdNvacantes = vacanteService.create(vacanteDTO);
        return new ResponseEntity<>(createdNvacantes, HttpStatus.CREATED);
    }

    @GetMapping("/edit/{nvacantes}")
    public ResponseEntity<VacanteDTO> getVacante(
            @PathVariable(name = "nvacantes") final Long nvacantes) {
        return ResponseEntity.ok(vacanteService.get(nvacantes));
    }

    @PutMapping("/edit/{nvacantes}")
    public ResponseEntity<Long> updateVacante(
            @PathVariable(name = "nvacantes") final Long nvacantes,
            @RequestBody @Valid final VacanteDTO vacanteDTO) {
        vacanteService.update(nvacantes, vacanteDTO);
        return ResponseEntity.ok(nvacantes);
    }

    @DeleteMapping("/delete/{nvacantes}")
    public ResponseEntity<Void> deleteVacante(
            @PathVariable(name = "nvacantes") final Long nvacantes) {
        
        vacanteService.delete(nvacantes);
        return ResponseEntity.noContent().build();
    }

}
