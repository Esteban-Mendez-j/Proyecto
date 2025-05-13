package com.miproyecto.proyecto.rest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.CandidatoResumenDTO;
import com.miproyecto.proyecto.model.PostuladoDTO;
import com.miproyecto.proyecto.service.CandidatoService;
import com.miproyecto.proyecto.service.PostuladoService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    //Candidatos postulados a una vacante (para empresa)
    @GetMapping("/lista/{nvacantes}")
    public ResponseEntity<Map<String,Object>> listaByNvacantes(
                @PathVariable(name = "nvacantes") String nvacantes, 
                @PageableDefault(page = 0, size = 10)
                Pageable pageable) {
        System.out.println(nvacantes);
        Map<String, Object> response = postuladoService.findByNvacantes(Long.parseLong(nvacantes), pageable); 
        return ResponseEntity.ok(response);
    }

    // lista de postulaciones de un candidato (para candidato )
    @GetMapping("/lista/candidato")
    public ResponseEntity<Map<String, Object>> listaByIdUsuario(
                HttpSession session,
                @PageableDefault(page = 0, size = 10)
                Pageable pageable) {

        // Extraer el ID del usuario desde el token JWT guardado en sesión
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        Map<String, Object> response =  postuladoService.findByIdUsuario(idUsuario, pageable);
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
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        // Verificar si ya está postulado
        if (postuladoService.findByNvacantesAndIdUsuario(nvacantes, idUsuario) != null) {
            response.put("status", "error");
            response.put("message", "Ya postulaste a esta vacante.");
            return ResponseEntity.badRequest().body(response);
        }

        // Verificar datos del perfil del candidato
        CandidatoResumenDTO candidatoResumenDTO = candidatoService.getCandidatoResumen(idUsuario);

        if (candidatoResumenDTO.getCurriculo() == null ) {
            response.put("status", "info");
            response.put("message", "Debes subir tu curriculo para postularte");
            return ResponseEntity.badRequest().body(response);
        }
        // Crear postulación
        PostuladoDTO postuladoDTO = new PostuladoDTO();
        postuladoDTO.setCandidato(candidatoResumenDTO);
        postuladoService.create(postuladoDTO, candidatoResumenDTO, nvacantes);

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

    @DeleteMapping("/delete/{nPostulacion}")
    public ResponseEntity<Void> deletePostulado(
            @PathVariable(name = "nPostulacion") final Long nPostulacion) {
        postuladoService.delete(nPostulacion);
        return ResponseEntity.noContent().build();
    }

}
