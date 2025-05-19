package com.miproyecto.proyecto.service;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Estudio;
import com.miproyecto.proyecto.model.EstudioDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.repos.EstudioRepository;
import com.miproyecto.proyecto.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class EstudioService {

    private final EstudioRepository estudioRepository;
    private final CandidatoRepository candidatoRepository;

    public EstudioService(final EstudioRepository estudioRepository,
            final CandidatoRepository candidatoRepository) {
        this.estudioRepository = estudioRepository;
        this.candidatoRepository = candidatoRepository;
    }

    public List<EstudioDTO> findAll() {
        final List<Estudio> estudios = estudioRepository.findAll(Sort.by("idEstudio"));
        return estudios.stream()
                .map(estudio -> mapToDTO(estudio, new EstudioDTO()))
                .toList();
    }

    @Transactional
    public void replaceEstudios(Long candidatoId, List<EstudioDTO> nuevosDTO) {

        // 1) Borrar todos los estudios actuales del candidato
        estudioRepository.deleteByIdUsuario_IdUsuario(candidatoId);
        
        List<Estudio> nuevos = nuevosDTO.stream()
                .map(dto -> mapToEntity(dto, new Estudio()))   // ① dto, ② entidad vacía
                .toList();


        // 3) Guardar la lista nueva
        estudioRepository.saveAll(nuevos);
    }


    // obtienen todos los estudios registrados con el mismo idUsuario 
    public List<EstudioDTO> getEstudiosByIdUsuario(final Long idUsuario) {
        final Candidato candidato = candidatoRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        final List<Estudio> idUsuarioEstudio = estudioRepository.findByIdUsuario(candidato);
        if (idUsuarioEstudio != null) {
            return idUsuarioEstudio.stream()
            .map(estudio -> mapToDTO(estudio, new EstudioDTO()))
            .toList();   
        }
        return null;
    }

    public EstudioDTO get(final Long idEstudio) {
        return estudioRepository.findById(idEstudio)
                .map(estudio -> mapToDTO(estudio, new EstudioDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final EstudioDTO estudioDTO) {
        final Estudio estudio = new Estudio();
        mapToEntity(estudioDTO, estudio);
        return estudioRepository.save(estudio).getIdEstudio();
    }

    public void update(final Long idEstudio, final EstudioDTO estudioDTO) {
        final Estudio estudio = estudioRepository.findById(idEstudio)
                .orElseThrow(NotFoundException::new);
        mapToEntity(estudioDTO, estudio);
        estudioRepository.save(estudio);
    }

    public void delete(final Long idEstudio) {
        estudioRepository.deleteById(idEstudio);
    }

    private EstudioDTO mapToDTO(final Estudio estudio, final EstudioDTO estudioDTO) {
        estudioDTO.setIdEstudio(estudio.getIdEstudio());
        estudioDTO.setTitulo(estudio.getTitulo());
        estudioDTO.setAcademia(estudio.getAcademia());
        estudioDTO.setIdUsuario(estudio.getIdUsuario() == null ? null : estudio.getIdUsuario().getIdUsuario());
        return estudioDTO;
    }

    private Estudio mapToEntity(final EstudioDTO estudioDTO, final Estudio estudio) {
        estudio.setTitulo(estudioDTO.getTitulo());
        estudio.setAcademia(estudioDTO.getAcademia());
        final Candidato idUsuario = estudioDTO.getIdUsuario() == null ? null : candidatoRepository.findById(estudioDTO.getIdUsuario())
                .orElseThrow(() -> new NotFoundException("idUsuario not found"));
        estudio.setIdUsuario(idUsuario);
        return estudio;
    }

}
