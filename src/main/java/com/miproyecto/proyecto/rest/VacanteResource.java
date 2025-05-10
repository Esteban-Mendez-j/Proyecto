package com.miproyecto.proyecto.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.miproyecto.proyecto.model.FiltroVacanteDTO;
import com.miproyecto.proyecto.model.VacanteDTO;
import com.miproyecto.proyecto.service.VacanteService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


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
    public ResponseEntity<Map<String,Object>> list(HttpSession session, 
        @PageableDefault(page = 0, size = 10) Pageable pageable) {

        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        Map<String, Object> response = vacanteService.findByIdUsuario(idUsuario, pageable);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/listar")
    public ResponseEntity<Map<String, Object>> listarVacantes(
        HttpSession session, @PageableDefault(page = 0, size = 10) Pageable pageable,
        @RequestBody FiltroVacanteDTO filtro) {

        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        filtro.setIdUsuario(Long.parseLong(jwtUtils.extractUsername(decodedJWT)));
        Map<String, Object> response = vacanteService.buscarVacantesConFiltros(filtro, pageable);
        return ResponseEntity.ok(response);
    }

    // @GetMapping("/popular/listar")
    // public ResponseEntity<Map<String, Object>> TopVacantes(
    //     HttpSession session) {

    //     String jwtToken = (String) session.getAttribute("jwtToken");
    //     DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
    //     Long idEmpresa = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
    //     List<VacanteDTO>vacantes = vacanteService.TopVacantesPorPostulados(idEmpresa);
        
    //     Map<String, Object> response = new HashMap<>();
    //     response.put("vacantes", vacantes);
    //     return ResponseEntity.ok(response);
    // }

    @GetMapping("/Top/listar")
    public ResponseEntity<Map<String, Object>> TopVacantesPorFechaSueldoExperiencia(
        HttpSession session) {
        List<VacanteDTO>vacantes = vacanteService.TopVacantesPorFechaSueldoExperiencia();
        
        Map<String, Object> response = new HashMap<>();
        response.put("vacantes", vacantes);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/listar/filtradas")
    public ResponseEntity<Map<String, Object>> listarVacantesFiltradas(
    HttpSession session,
    @PageableDefault(page = 0, size = 10) Pageable pageable,
    @RequestBody FiltroVacanteDTO filtro ) {
        filtro.setRolUser("CANDIDATO");
        Map<String, Object> response = vacanteService.buscarVacantesConFiltros(filtro, pageable);
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
        Map<String, Object> response = new HashMap<>();
        response.put("vacanteSeleccionada", vacanteSeleccionada);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createVacante(
            @RequestBody @Valid final VacanteDTO vacanteDTO,
            @CookieValue(name = "jwtToken", required = false) String jwtToken) {
                
        Map<String, Object> response = new HashMap<>();
        if (jwtToken != null) {
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));    
            vacanteDTO.setIdUsuario(idUsuario);
        }
        vacanteService.create(vacanteDTO);
        response.put("status", HttpStatus.CREATED.value());
        response.put("mensaje", vacanteDTO.getTipo()+" creada con exito!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit/{nvacantes}")
    public ResponseEntity<VacanteDTO> getVacante(
            @PathVariable(name = "nvacantes") final Long nvacantes) {
        return ResponseEntity.ok(vacanteService.get(nvacantes));
    }

    @PutMapping("/edit/{nvacantes}")
    public ResponseEntity<Map<String, Object>> updateVacante(
            @PathVariable(name = "nvacantes") final Long nvacantes,
            @RequestBody @Valid final VacanteDTO vacanteDTO) {
        Map<String, Object> response = new HashMap<>();
        vacanteService.update(nvacantes, vacanteDTO);
        response.put("status", HttpStatus.CREATED.value());
        response.put("mensaje", vacanteDTO.getTipo()+" actualizada con exito!");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{nvacantes}")
    public ResponseEntity<Void> deleteVacante(
            @PathVariable(name = "nvacantes") final Long nvacantes) {
        
        vacanteService.delete(nvacantes);
        return ResponseEntity.noContent().build();
    }

}
