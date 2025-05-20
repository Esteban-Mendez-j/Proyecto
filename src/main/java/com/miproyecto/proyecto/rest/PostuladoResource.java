package com.miproyecto.proyecto.rest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.CandidatoResumenDTO;
import com.miproyecto.proyecto.model.PostuladoDTO;
import com.miproyecto.proyecto.model.VacanteDTO;
import com.miproyecto.proyecto.service.CandidatoService;
import com.miproyecto.proyecto.service.PostuladoService;
import com.miproyecto.proyecto.service.VacanteService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/postulados", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostuladoResource {

    private final PostuladoService postuladoService;
    private final CandidatoService candidatoService;
    private final JwtUtils jwtUtils;
    private final VacanteService vacanteService;

    public PostuladoResource(PostuladoService postuladoService, CandidatoService candidatoService, JwtUtils jwtUtils,
            VacanteService vacanteService) {
        this.postuladoService = postuladoService;
        this.candidatoService = candidatoService;
        this.jwtUtils = jwtUtils;
        this.vacanteService = vacanteService;
    }

    @GetMapping
    public ResponseEntity<List<PostuladoDTO>> getAllPostulados() {
        return ResponseEntity.ok(postuladoService.findAll());
    }

    //Candidatos postulados a una vacante (para empresa)
    @GetMapping("/lista")
    public ResponseEntity<Map<String, Object>> listaByNvacantes(
        @RequestParam(name = "nvacantes") Long nvacantes,
        @RequestParam(name = "estado", required = false) String estado,
        @RequestParam(name = "fechaMinima", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaMinima,
        @RequestParam(name = "nombreCandidato", required = false) String nombreCandidato,
        @PageableDefault(page = 0, size = 10) Pageable pageable,
        @CookieValue(name = "jwtToken", required = false) String jwtToken) {

        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
        String rolPrincipal = decodedJWT.getClaim("rolPrincipal").asString();
        VacanteDTO vacante = vacanteService.findByIdUsuarioAndNvacante(idUsuario, nvacantes);
        
        if( vacante == null && rolPrincipal.equalsIgnoreCase("EMPRESA")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Map<String, Object> response = postuladoService.findByNvacantes(nvacantes, estado, fechaMinima, nombreCandidato, pageable);
        return ResponseEntity.ok(response);
    }


    // lista de postulaciones de un candidato (para candidato )
    @GetMapping("/lista/candidato")
    public ResponseEntity<Map<String, Object>> listaByIdUsuario(
            HttpSession session,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDate fechaMinima,
            @RequestParam(required = false) String tituloVacante,
            @RequestParam(required = false) String empresa
    ) {
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        Map<String, Object> response = postuladoService.findByIdUsuario(
            idUsuario, estado, tituloVacante, empresa, fechaMinima, pageable
        );
        return ResponseEntity.ok(response);
    }




    @PostMapping("/add/{nvacantes}")
    public ResponseEntity<Map<String, Object>> addPostulacion(
            @PathVariable Long nvacantes,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        // Verificar si ya existe una postulación activa
        PostuladoDTO postuladoDTO = postuladoService
                .findByNvacantesAndIdUsuario(nvacantes, idUsuario);

        if (postuladoDTO != null && postuladoDTO.isActive()) {
            response.put("status", "error");
            response.put("message", "Ya postulaste a esta vacante.");
            return ResponseEntity.badRequest().body(response);
        }

        //  Verificar datos del perfil (currículum obligatorio)
        CandidatoResumenDTO candidatoResumenDTO = candidatoService
                .getCandidatoResumen(idUsuario);

        if (candidatoResumenDTO.getCurriculo() == null) {
            response.put("status", "info");
            response.put("message", "Debes subir tu currículum para postularte.");
            return ResponseEntity.badRequest().body(response);
        }

        //  Si existe postuladoDTO lo reactivamos; de lo contrario creamos uno nuevo
        if (postuladoDTO != null ) {
            postuladoService.cambiarEstado(postuladoDTO, true);
        } else {
            PostuladoDTO nuevo = new PostuladoDTO();
            nuevo.setCandidato(candidatoResumenDTO);
            postuladoService.create(nuevo, candidatoResumenDTO, nvacantes);
        }

        response.put("status", "success");
        response.put("message", "Postulación realizada con éxito.");
        return ResponseEntity.ok(response);
    }


    @GetMapping("/edit/{nPostulacion}")
    public ResponseEntity<PostuladoDTO> getPostulado(
            @PathVariable(name = "nPostulacion") final Long nPostulacion) {
        return ResponseEntity.ok(postuladoService.get(nPostulacion));
    }

    @PutMapping("/edit/{nPostulacion}")
    public ResponseEntity<Long> updatePostulado(
            @PathVariable(name = "nPostulacion") final Long nPostulacion,
            @RequestBody final PostuladoDTO postuladoDTO) {
        postuladoService.update(nPostulacion, postuladoDTO);
        return ResponseEntity.ok(nPostulacion);
    }

    @PatchMapping("/cancelar/{nPostulacion}")
    public ResponseEntity<Void> cancelarPostulado(
            @PathVariable Long nPostulacion,
            @RequestParam(name = "estado") Boolean estado,
            @RequestParam(name = "nvacante") Long nvacantes) {

        postuladoService.cancelarPostulacion(nPostulacion, estado, nvacantes);
        return ResponseEntity.noContent().build();   // 204 No Content
    } 
}
