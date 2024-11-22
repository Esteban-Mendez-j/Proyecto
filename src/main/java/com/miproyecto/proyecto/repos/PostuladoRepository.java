package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Postulado;
import com.miproyecto.proyecto.domain.Vacante;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PostuladoRepository extends JpaRepository<Postulado, Long> {

    List<Postulado> findByNvacante(Vacante vacante);
    
    Optional<Postulado> findByNvacante_NvacantesAndIdUsuario_IdUsuario(Long nvacanteId, Long idUsuarioId);

    List<Postulado> findByIdUsuario(Candidato candidato);

    Postulado findFirstByNvacante(Vacante vacante);

    Postulado findFirstByIdUsuario(Candidato candidato);

}
