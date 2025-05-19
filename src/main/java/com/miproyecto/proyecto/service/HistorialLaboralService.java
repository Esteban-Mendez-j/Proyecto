package com.miproyecto.proyecto.service;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.HistorialLaboral;
import com.miproyecto.proyecto.model.HistorialLaboralDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.repos.HistorialLaboralRepository;
import com.miproyecto.proyecto.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class HistorialLaboralService {

    private final HistorialLaboralRepository historialLaboralRepository;
    private final CandidatoRepository candidatoRepository;

    public HistorialLaboralService(final HistorialLaboralRepository historialLaboralRepository,
            final CandidatoRepository candidatoRepository) {
        this.historialLaboralRepository = historialLaboralRepository;
        this.candidatoRepository = candidatoRepository;
    }

    public List<HistorialLaboralDTO> findAll() {
        final List<HistorialLaboral> historialLaborals = historialLaboralRepository.findAll(Sort.by("iDHistorial"));
        return historialLaborals.stream()
                .map(historialLaboral -> mapToDTO(historialLaboral, new HistorialLaboralDTO()))
                .toList();
    }

    public List<HistorialLaboralDTO> getHistorialByIdUsuario(final Long idUsuario) {
        final Candidato candidato = candidatoRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        final List<HistorialLaboral> idUsuarioHistorialLaboral =  historialLaboralRepository.findByIdUsuario(candidato);
        if (idUsuarioHistorialLaboral != null) {
            return idUsuarioHistorialLaboral.stream()
            .map(historialLaboral -> mapToDTO(historialLaboral, new HistorialLaboralDTO()))
            .toList();
        }
        return null;
    }


    public HistorialLaboralDTO get(final Long iDHistorial) {
        return historialLaboralRepository.findById(iDHistorial)
                .map(historialLaboral -> mapToDTO(historialLaboral, new HistorialLaboralDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final HistorialLaboralDTO historialLaboralDTO) {
        final HistorialLaboral historialLaboral = new HistorialLaboral();
        mapToEntity(historialLaboralDTO, historialLaboral);
        return historialLaboralRepository.save(historialLaboral).getIDHistorial();
    }

    public void update(final Long iDHistorial, final HistorialLaboralDTO historialLaboralDTO) {
        final HistorialLaboral historialLaboral = historialLaboralRepository.findById(iDHistorial)
                .orElseThrow(NotFoundException::new);
        mapToEntity(historialLaboralDTO, historialLaboral);
        historialLaboralRepository.save(historialLaboral);
    }

    public void delete(final Long iDHistorial) {
        historialLaboralRepository.deleteById(iDHistorial);
    }

    @Transactional
    public void replaceHistorial(Long candidatoId, List<HistorialLaboralDTO> nuevosDTO) {

        historialLaboralRepository.deleteByIdUsuario_IdUsuario(candidatoId);
        
        List<HistorialLaboral> nuevos = nuevosDTO.stream()
                .map(dto -> mapToEntity(dto, new HistorialLaboral()))   // ① dto, ② entidad vacía
                .toList();

        historialLaboralRepository.saveAll(nuevos);
    }

    private HistorialLaboralDTO mapToDTO(final HistorialLaboral historialLaboral,
            final HistorialLaboralDTO historialLaboralDTO) {
        historialLaboralDTO.setiDHistorial(historialLaboral.getIDHistorial());
        historialLaboralDTO.setTitulo(historialLaboral.getTitulo());
        historialLaboralDTO.setEmpresa(historialLaboral.getEmpresa());
        historialLaboralDTO.setIdUsuario(historialLaboral.getIdUsuario() == null ? null : historialLaboral.getIdUsuario().getIdUsuario());
        return historialLaboralDTO;
    }

    private HistorialLaboral mapToEntity(final HistorialLaboralDTO historialLaboralDTO,
            final HistorialLaboral historialLaboral) {
        historialLaboral.setTitulo(historialLaboralDTO.getTitulo());
        historialLaboral.setEmpresa(historialLaboralDTO.getEmpresa());
        final Candidato idUsuario = historialLaboralDTO.getIdUsuario() == null ? null : candidatoRepository.findById(historialLaboralDTO.getIdUsuario())
                .orElseThrow(() -> new NotFoundException("idUsuario not found"));
        historialLaboral.setIdUsuario(idUsuario);
        return historialLaboral;
    }

}
