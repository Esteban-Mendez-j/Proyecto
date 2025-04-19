package com.miproyecto.proyecto.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miproyecto.proyecto.domain.Apelacion;
import com.miproyecto.proyecto.model.ApelacionDTO;
import com.miproyecto.proyecto.repos.ApelacionRepository;
import com.miproyecto.proyecto.repos.UsuarioRepository;
import com.miproyecto.proyecto.repos.VacanteRepository;
import com.miproyecto.proyecto.util.NotFoundException;

@Service
public class ApelacionService {

    @Autowired
    private ApelacionRepository apelacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VacanteRepository vacanteRepository;

    // Obtener todas las apelaciones
    public List<ApelacionDTO> findAll() {
        return apelacionRepository.findAll()
                .stream()
                .map(apelacion -> mapToDTO(apelacion, new ApelacionDTO()))
                .collect(Collectors.toList());
    }

    // Obtener apelacion por ID
    public ApelacionDTO get(final Long id) {
        return apelacionRepository.findById(id)
                .map(apelacion -> mapToDTO(apelacion, new ApelacionDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public List<ApelacionDTO> findByEstado(String estado) {
        return apelacionRepository.findByEstadoIgnoreCase(estado)
                .stream().map(apelacion -> mapToDTO(apelacion, new ApelacionDTO())).collect(Collectors.toList());
    }

    public List<ApelacionDTO> findByUsuarioId(Long idUsuario) {
        return apelacionRepository.findByUsuario_IdUsuario(idUsuario)
                .stream().map(apelacion -> mapToDTO(apelacion, new ApelacionDTO())).collect(Collectors.toList());
    }

    public List<ApelacionDTO> findByEsPorCuenta(Boolean esPorCuenta) {
        return apelacionRepository.findByEsPorCuenta(esPorCuenta)
                .stream().map(apelacion -> mapToDTO(apelacion, new ApelacionDTO())).collect(Collectors.toList());
    }

    // Crear apelacion
    public Long  create(final ApelacionDTO apelacionDTO) {
        final Apelacion apelacion = new Apelacion();
        mapToEntity(apelacionDTO, apelacion, true);
        return apelacionRepository.save(apelacion).getId();
    }

    // Actualizar apelacion
    public void update(final Long id, final ApelacionDTO apelacionDTO) {
        final Apelacion apelacion = apelacionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(apelacionDTO, apelacion, false);
        apelacionRepository.save(apelacion);
    }

    // Eliminar apelacion
    public void delete(final Long id) {
        apelacionRepository.deleteById(id);
    }

    public ApelacionDTO mapToDTO(final Apelacion apelacion, final ApelacionDTO apelacionDTO) {
        apelacionDTO.setId(apelacion.getId());
        apelacionDTO.setMensaje(apelacion.getMensaje());
        apelacionDTO.setEstado(apelacion.getEstado());
        apelacionDTO.setEsPorCuenta(apelacion.getEsPorCuenta());
        apelacionDTO.setComentarioAdmin(apelacion.getComentarioAdmin());
        apelacionDTO.setFechaCreacion(apelacion.getFechaCreacion());

        if (apelacion.getUsuario() != null)
            apelacionDTO.setIdUsuario(apelacion.getUsuario().getIdUsuario());

        if (apelacion.getVacante() != null)
            apelacionDTO.setIdVacante(apelacion.getVacante().getNvacantes());

        if (apelacion.getAdmin() != null)
            apelacionDTO.setIdAdmin(apelacion.getAdmin().getIdUsuario());

        return apelacionDTO;
    }

    public Apelacion mapToEntity(final ApelacionDTO apelacionDTO, final Apelacion apelacion, boolean esCreacion) {
        apelacion.setMensaje(apelacionDTO.getMensaje());
        apelacion.setEstado(apelacionDTO.getEstado());
        apelacion.setEsPorCuenta(apelacionDTO.getEsPorCuenta());
        apelacion.setComentarioAdmin(apelacionDTO.getComentarioAdmin());

        if (esCreacion)
            apelacion.setFechaCreacion(LocalDateTime.now());

        if (apelacionDTO.getIdUsuario() != null)
            apelacion.setUsuario(usuarioRepository.findById(apelacionDTO.getIdUsuario()).orElseThrow(NotFoundException::new));

        if (apelacionDTO.getIdVacante() != null)
            apelacion.setVacante(vacanteRepository.findById(apelacionDTO.getIdVacante()).orElseThrow(NotFoundException::new));

        if (apelacionDTO.getIdAdmin() != null)
            apelacion.setAdmin(usuarioRepository.findById(apelacionDTO.getIdAdmin()).orElseThrow(NotFoundException::new));

        return apelacion;
    }

}

    

