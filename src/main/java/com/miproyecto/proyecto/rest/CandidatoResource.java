package com.miproyecto.proyecto.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.CandidatoDTO;
import com.miproyecto.proyecto.model.PostuladoDTO;
import com.miproyecto.proyecto.model.ValidationGroups;
import com.miproyecto.proyecto.service.CandidatoService;
import com.miproyecto.proyecto.service.EstudioService;
import com.miproyecto.proyecto.service.HistorialLaboralService;
import com.miproyecto.proyecto.service.PostuladoService;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;


@RestController
@RequestMapping(value = "/api/candidatos", produces = MediaType.APPLICATION_JSON_VALUE)
public class CandidatoResource {
    
    private final CandidatoService candidatoService;
    private final JwtUtils jwtUtils;
    private final PostuladoService postuladoService;
    private final EstudioService estudioService;
    private final HistorialLaboralService historialLaboralService;
    private final UsuarioService usuarioService; 

    public CandidatoResource(CandidatoService candidatoService, JwtUtils jwtUtils, PostuladoService postuladoService,
            EstudioService estudioService, HistorialLaboralService historialLaboralService,
            UsuarioService usuarioService) {
        this.candidatoService = candidatoService;
        this.jwtUtils = jwtUtils;
        this.postuladoService = postuladoService;
        this.estudioService = estudioService;
        this.historialLaboralService = historialLaboralService;
        this.usuarioService = usuarioService;
    }
    
    @GetMapping("/perfil")
    public ResponseEntity<Map<String, Object>> mostrarPerfil(
        @RequestParam(name = "idUsuario", required = false) Long idUsuario,
        @RequestParam(name = "nPostulacion", required = false) Long nPostulacion,
        Model model, HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        String rol = null; 
        try {
            // 1. Obtener ID del token si no viene por parámetro
            if (idUsuario == null) {
                String jwtToken = (String) session.getAttribute("jwtToken");
                if (jwtToken == null) {
                    response.put("error", "No se encontró token de sesión");
                    return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
                }

                DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
                idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
                rol = decodedJWT.getClaim("rolPrincipal").asString();
            }

            // 2. Validar si tiene permiso de ver la postulación
            if (nPostulacion != null && "EMPRESA".equalsIgnoreCase(rol) ) {
                PostuladoDTO postulado = postuladoService.get(nPostulacion);
                if (postulado == null || postulado.getCandidato() == null) {
                    response.put("error", "Postulación no encontrada o inválida");
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
                }
                if (!postulado.getCandidato().getId().equals(idUsuario)) {
                    response.put("error", "No tienes permiso para acceder");
                    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
                }
            }

            // 3. Obtener candidato
            CandidatoDTO candidatoDTO = candidatoService.get(idUsuario);
            if (candidatoDTO == null) {
                response.put("error", "Candidato no encontrado");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.put("estudios", estudioService.getEstudiosByIdUsuario(idUsuario));
            response.put("historialLaboral", historialLaboralService.getHistorialByIdUsuario(idUsuario));
            response.put("candidato", candidatoDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error interno: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> createCandidato(@RequestBody  @Valid  CandidatoDTO candidatoDTO) {
        Map<String, Object> response = new HashMap<>();
        candidatoService.create(candidatoDTO);
        response.put("status", HttpStatus.CREATED.value());
        response.put("mensaje", "Usuario creado con exito!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit/{idUsuario}")
    public ResponseEntity<CandidatoDTO> getCandidato(
            @PathVariable(name = "idCandidato") final Long idCandidato) {
        return ResponseEntity.ok(candidatoService.get(idCandidato));
    }

    @PutMapping(value = "/edit/{idUsuario}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> editCandidato(
            @RequestPart("candidato") @Validated({ValidationGroups.OnUpdate.class, Default.class}) CandidatoDTO candidatoDTO,
            @RequestPart(name = "img", required = false) MultipartFile imagen,
            @RequestPart(name = "pdf", required = true) MultipartFile curriculo) {

        Map<String, Object> response = new HashMap<>();
        Long idUsuario = candidatoDTO.getIdUsuario();

        try {
            
            if (imagen != null && !imagen.isEmpty()) {
                if (candidatoDTO.getImagen() != null && !candidatoDTO.getImagen().isEmpty()) {

                    usuarioService.eliminarArchivo(candidatoDTO.getImagen(), true);
                }
                String rutaImagen = usuarioService.guardarArchivo(imagen, idUsuario);
                candidatoDTO.setImagen(rutaImagen);
            }

            // Verificar si se ha proporcionado un nuevo curriculo
            if (curriculo != null && !curriculo.isEmpty()) {
                if (candidatoDTO.getCurriculo() != null && !candidatoDTO.getCurriculo().isEmpty() ) {
                    usuarioService.eliminarArchivo(candidatoDTO.getCurriculo(), false);
                }
                String rutacurriculo = usuarioService.guardarArchivo(curriculo, idUsuario);
                candidatoDTO.setCurriculo(rutacurriculo);
            }
            // Actualizar los datos
            candidatoService.update(idUsuario, candidatoDTO);
            response.put("status", HttpStatus.OK.value());
            response.put("mensaje", "Candidato actualizado correctamente.");
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("mensaje", "Error al guardar la imagen.");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("mensaje", "Error al actualizar el candidato.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
