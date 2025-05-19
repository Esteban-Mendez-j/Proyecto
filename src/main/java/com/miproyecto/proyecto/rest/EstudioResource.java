package com.miproyecto.proyecto.rest;

import com.miproyecto.proyecto.model.EstudioDTO;
import com.miproyecto.proyecto.service.EstudioService;
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
@RequestMapping(value = "/api/estudios", produces = MediaType.APPLICATION_JSON_VALUE)
public class EstudioResource {

    private final EstudioService estudioService;

    public EstudioResource(final EstudioService estudioService) {
        this.estudioService = estudioService;
    }

    @GetMapping
    public ResponseEntity<List<EstudioDTO>> getAllEstudios() {
        return ResponseEntity.ok(estudioService.findAll());
    }
    

    @PostMapping("/add")
    public ResponseEntity<Long> createEstudio(@RequestBody @Valid final EstudioDTO estudioDTO) {
        final Long createdIdEstudio = estudioService.create(estudioDTO);
        return new ResponseEntity<>(createdIdEstudio, HttpStatus.CREATED);
    }

    @GetMapping("/edit/{idEstudio}")
    public ResponseEntity<EstudioDTO> getEstudio(
            @PathVariable(name = "idEstudio") final Long idEstudio) {
        return ResponseEntity.ok(estudioService.get(idEstudio));
    }

    @PutMapping("/replace/{candidatoId}")
    public ResponseEntity<Void> replaceEstudios(
            @PathVariable Long candidatoId,
            @RequestBody List<EstudioDTO> estudios) {
        
            
        estudioService.replaceEstudios(candidatoId, estudios);
        return ResponseEntity.ok().build();   
    }

    @PutMapping("/edit/{idEstudio}")
    public ResponseEntity<Long> updateEstudio(
            @PathVariable(name = "idEstudio") final Long idEstudio,
            @RequestBody @Valid final EstudioDTO estudioDTO) {
        estudioService.update(idEstudio, estudioDTO);
        return ResponseEntity.ok(idEstudio);
    }

    @DeleteMapping("/delete/{idEstudio}")
    public ResponseEntity<Void> deleteEstudio(
            @PathVariable(name = "idEstudio") final Long idEstudio) {
        estudioService.delete(idEstudio);
        return ResponseEntity.noContent().build();
    }

}
