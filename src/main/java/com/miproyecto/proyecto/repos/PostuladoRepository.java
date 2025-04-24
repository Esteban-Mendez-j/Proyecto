package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Postulado;
import com.miproyecto.proyecto.domain.Vacante;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostuladoRepository extends JpaRepository<Postulado, Long>{

    Page<Postulado> findByNvacante(Vacante vacante, Pageable pageable);
    
    Optional<Postulado> findByNvacante_NvacantesAndIdUsuario_IdUsuario(Long nvacanteId, Long idUsuarioId);

    Page<Postulado> findByIdUsuario(Candidato candidato, Pageable pageable);

    Postulado findFirstByNvacante(Vacante vacante);

    Postulado findFirstByIdUsuario(Candidato candidato);

}
