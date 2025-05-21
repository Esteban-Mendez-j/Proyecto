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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    //para empresas
    @PostMapping("/listar")
    public ResponseEntity<Map<String, Object>> listarVacantes(
        HttpSession session, @PageableDefault(page = 0, size = 10) Pageable pageable,
        @RequestBody FiltroVacanteDTO filtro) {

        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        filtro.setIdUsuario(Long.parseLong(jwtUtils.extractUsername(decodedJWT)));
        Long idUsuarioPostulacion = 0L;
        Map<String, Object> response = vacanteService.buscarVacantesConFiltros(idUsuarioPostulacion,filtro, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular/listar")
    public ResponseEntity<Map<String, Object>> TopVacantes(
        HttpSession session) {

        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idEmpresa = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
        List<VacanteDTO>vacantes = vacanteService.TopVacantesPorPostulados(idEmpresa);
        
        Map<String, Object> response = new HashMap<>();
        response.put("vacantes", vacantes);
        return ResponseEntity.ok(response);
    }

    // para candidatos e invitados 
    @GetMapping("/Top/listar")
    public ResponseEntity<Map<String, Object>> TopVacantesPorFechaSueldoExperiencia(
        @CookieValue(name = "jwtToken", required = false) String jwtToken) {
        Long idUsuario=0L;
        if (jwtToken != null) {
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));    
        }
        
        List<VacanteDTO>vacantes = vacanteService.TopVacantesPorFechaSueldoExperiencia(idUsuario);
        Map<String, Object> response = new HashMap<>();
        response.put("vacantes", vacantes);
        return ResponseEntity.ok(response);
    }

    // para candidatos e invitados
    @PostMapping("/listar/filtradas")
    public ResponseEntity<Map<String, Object>> listarVacantesFiltradas(
    HttpSession session,
    @PageableDefault(page = 0, size = 10) Pageable pageable,@CookieValue(name = "jwtToken", required = false) String jwtToken,
    @RequestBody FiltroVacanteDTO filtro ) {
        Long idUsuario=0L;
        if (jwtToken != null) {
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));    
        }
        filtro.setActive(true);
        Map<String, Object> response = vacanteService.buscarVacantesConFiltros(idUsuario,filtro, pageable);
        return ResponseEntity.ok(response);
    }
 
    @GetMapping("/seleccion/{nvacantes}")
    public ResponseEntity<Map<String, Object>> seleccionVacante(
            @PathVariable(name = "nvacantes") Long nvacantes,
            @CookieValue(name = "jwtToken", required = false) String jwtToken) {
        
        Long idUsuario=0L;
        
        if (jwtToken != null) {
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            String rolPrincipal = decodedJWT.getClaim("rolPrincipal").asString();
            if ("CANDIDATO".equals(rolPrincipal)) {
                idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
            }
        }
        VacanteDTO vacanteSeleccionada = vacanteService.get(idUsuario, nvacantes);

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
            HttpSession session) {
                
        Map<String, Object> response = new HashMap<>();
        String jwtToken = (String) session.getAttribute("jwtToken");
        if (jwtToken != null) {
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));    
            vacanteDTO.setIdUsuario(idUsuario);
            System.out.println("id jwt"+ idUsuario);
        }
        vacanteService.create(vacanteDTO);
        response.put("status", HttpStatus.CREATED.value());
        response.put("mensaje", vacanteDTO.getTipo()+" creada con exito!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit/{nvacantes}")
    public ResponseEntity<VacanteDTO> getVacante(
            @PathVariable(name = "nvacantes") final Long nvacantes) {
        Long idUsuario = 0L;
        return ResponseEntity.ok(vacanteService.get(idUsuario,nvacantes));
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

    @PutMapping("/estado/{idVacante}")
    public ResponseEntity<Void> cambiarEstadoVacante(
            @PathVariable Long idVacante,
            @RequestParam boolean estado
    ) {
        vacanteService.cambiarEstado(idVacante, estado);
        return ResponseEntity.ok().build();
    }
}
