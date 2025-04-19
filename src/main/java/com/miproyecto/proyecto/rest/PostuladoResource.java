package com.miproyecto.proyecto.rest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.CandidatoDTO;
import com.miproyecto.proyecto.model.PostuladoDTO;
import com.miproyecto.proyecto.service.CandidatoService;
import com.miproyecto.proyecto.service.PostuladoService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@RequestMapping(value = "/api/postulados", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostuladoResource {

    private final PostuladoService postuladoService;
    private final CandidatoService candidatoService;
    private final JwtUtils jwtUtils;

    public PostuladoResource(PostuladoService postuladoService, CandidatoService candidatoService, JwtUtils jwtUtils) {
        this.postuladoService = postuladoService;
        this.candidatoService = candidatoService;
        this.jwtUtils = jwtUtils;
    }


    @GetMapping
    public ResponseEntity<List<PostuladoDTO>> getAllPostulados() {
        return ResponseEntity.ok(postuladoService.findAll());
    }


    @GetMapping("/lista/{nvacantes}")
    public ResponseEntity<Map<String,Object>> listaByNvacantes(@PathVariable(name = "nvacantes") final Long nvacantes) {
        
        Map<String, Object> response = new HashMap<>();
        // Obtener las postulaciones
        response.put("postulados", postuladoService.findByNvacantes(nvacantes));  // Se añaden los postulados al modelo

        // Obtener los candidatos únicos asociados a estas postulaciones
        Map<Long, CandidatoDTO> candidatosMap = postuladoService.obtenerCandidatosUnicosPorVacante(nvacantes);
        response.put("candidatos", candidatosMap);  // Añadir el mapa completo de candidatos al modelo

        return ResponseEntity.ok(response);
    }

    @GetMapping("/lista/candidato")
    public ResponseEntity<Map<String, Object>> listaByIdUsuario(HttpSession session) {

        // Extraer el ID del usuario desde el token JWT guardado en sesión
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        Map<String, Object> response = new HashMap<>();

        // Obtener postulados y vacantes asociadas
        response.put("postulados", postuladoService.findByIdUsuario(idUsuario));
        response.put("vacantes", postuladoService.findVacantesByIdUsuario(idUsuario));

        return ResponseEntity.ok(response);
    }


    @PostMapping("/add/{nvacantes}")
    public ResponseEntity<Map<String, Object>> addPostulacion(
            @PathVariable("nvacantes") Long nvacantes,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        // Obtener ID del usuario desde el token JWT en sesión
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuarioId = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        // Verificar si ya está postulado
        if (postuladoService.findByNvacantesAndIdUsuario(nvacantes, idUsuarioId) != null) {
            response.put("status", "error");
            response.put("message", "Ya postulaste a esta vacante.");
            return ResponseEntity.badRequest().body(response);
        }

        // Verificar datos del perfil del candidato
        CandidatoDTO candidatoDTO = candidatoService.get(idUsuarioId);
        boolean estudio = candidatoService.estudiosExist(idUsuarioId);

        if (candidatoDTO.getDescripcion() == null || !estudio || candidatoDTO.getTelefono() == null) {
            response.put("status", "info");
            response.put("message", "Completa tu perfil con descripción, teléfono y al menos un estudio antes de postularte.");
            return ResponseEntity.badRequest().body(response);
        }

        // Crear postulación
        PostuladoDTO postuladoDTO = new PostuladoDTO();
        postuladoDTO.setIdUsuario(idUsuarioId);
        postuladoDTO.setNvacante(nvacantes);
        postuladoDTO.setFechaPostulacion(LocalDate.now());
        postuladoDTO.setEstado("Espera");

        postuladoService.create(postuladoDTO);

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
            @RequestBody @Valid final PostuladoDTO postuladoDTO) {
        postuladoService.update(nPostulacion, postuladoDTO);
        return ResponseEntity.ok(nPostulacion);
    }

    @DeleteMapping("/delete/{nPostulacion}")
    public ResponseEntity<Void> deletePostulado(
            @PathVariable(name = "nPostulacion") final Long nPostulacion) {
        postuladoService.delete(nPostulacion);
        return ResponseEntity.noContent().build();
    }

}
