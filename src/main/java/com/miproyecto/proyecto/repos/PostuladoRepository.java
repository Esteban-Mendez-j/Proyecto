package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Postulado;
import com.miproyecto.proyecto.domain.Vacante;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PostuladoRepository extends JpaRepository<Postulado, Long>{

    Page<Postulado> findByVacante(Vacante vacante, Pageable pageable); 

    Optional<Postulado> findByVacante_NvacantesAndCandidato_IdUsuario(Long vacanteId, Long idUsuarioId);  // Cambiar Nvacante a Vacante y asegurar que los parámetros sean correctos

    Page<Postulado> findByCandidato(Candidato candidato, Pageable pageable);

    Postulado findFirstByVacante(Vacante vacante);  

    Postulado findFirstByCandidato(Candidato candidato);
    
    @Modifying
    @Query("UPDATE Postulado p SET p.vacanteIsActive = :estado WHERE p.vacante.nvacantes = :Nvacante")
    int actualizarEstadoPostulacionesPorVacante(@Param("Nvacante") Long Nvacante, @Param("estado") boolean estado);

    @Modifying
    @Query("UPDATE Postulado p SET p.isActive = :estado WHERE p.candidato.idUsuario = :idUsuario")
    int actualizarEstadoPostulacionesPorUsuario(@Param("idUsuario") Long idUsuario, @Param("estado") boolean estado);

    // Actualiza el estado de la postulación (isActive) para un determinado postulado
    // @Modifying
    // @Query("UPDATE Postulado p SET p.isActive = :estado WHERE p.nPostulacion = :nPostulacion")
    // int actualizarEstadoPostulacion(@Param("nPostulacion") Long nPostulacion, @Param("estado") boolean estado);

    // // Actualiza el estado de la vacante asociada a la postulación (vacanteIsActive)
    // @Modifying
    // @Query("UPDATE Postulado p SET p.vacanteIsActive = :estado WHERE p.nPostulacion = :nPostulacion")
    // int actualizarEstadoVacantePostulacion(@Param("nPostulacion") Long nPostulacion, @Param("estado") boolean estado);
}
