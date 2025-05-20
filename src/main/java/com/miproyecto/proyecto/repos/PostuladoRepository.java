package com.miproyecto.proyecto.repos;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Postulado;
import com.miproyecto.proyecto.domain.Vacante;

@Repository
@Transactional
public interface PostuladoRepository extends JpaRepository<Postulado, Long>{

    @Query("""
        SELECT p FROM Postulado p
        WHERE p.vacante = :vacante
            AND (:estado IS NULL OR p.estado = :estado)
            AND (:fechaMinima IS NULL OR p.fechaPostulacion >= :fechaMinima)
            AND (:nombreCandidato IS NULL OR LOWER(p.candidato.nombre) LIKE LOWER(CONCAT('%', :nombreCandidato, '%')))
    """)
    Page<Postulado> buscarPorFiltros(@Param("vacante") Vacante vacante, @Param("estado") String estado,
        @Param("fechaMinima") LocalDate fechaMinima,
        @Param("nombreCandidato") String nombreCandidato,
        Pageable pageable
    );


    @Query("SELECT p FROM Postulado p " +
        "JOIN p.vacante v " +
        "JOIN v.idUsuario e " +
        "WHERE (:idCandidato IS NULL OR p.candidato.id = :idCandidato) " +
        "AND (:estado IS NULL OR LOWER(p.estado) = LOWER(:estado)) " +
        "AND (:tituloVacante IS NULL OR LOWER(v.titulo) LIKE LOWER(CONCAT('%', :tituloVacante, '%'))) " +
        "AND (:nombreEmpresa IS NULL OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :nombreEmpresa, '%'))) " +
        "AND (:fechaMinima IS NULL OR p.fechaPostulacion >= :fechaMinima) " +
        "AND p.isActive = true " +
        "AND p.vacanteIsActive = true")
    Page<Postulado> buscarPostulacionesFiltradas(
        @Param("idCandidato") Long idCandidato,
        @Param("estado") String estado,
        @Param("tituloVacante") String tituloVacante,
        @Param("nombreEmpresa") String nombreEmpresa,
        @Param("fechaMinima") LocalDate fechaMinima,
        Pageable pageable
    );


    Optional<Postulado> findByCandidatoAndVacante(Candidato candidato, Vacante vacante);  // Cambiar Nvacante a Vacante y asegurar que los parámetros sean correctos

    Page<Postulado> findByCandidato(Candidato candidato, Pageable pageable);

    Postulado findFirstByVacante(Vacante vacante);  

    Postulado findFirstByCandidato(Candidato candidato);
    
    @Modifying
    @Query("UPDATE Postulado p SET p.vacanteIsActive = :estado WHERE p.vacante.nvacantes = :Nvacante")
    int actualizarEstadoPostulacionesPorVacante(@Param("Nvacante") Long Nvacante, @Param("estado") boolean estado);

    @Modifying
    @Query("UPDATE Postulado p SET p.isActive = :estado WHERE p.candidato.idUsuario = :idUsuario")
    int actualizarEstadoPostulacionesPorUsuario(@Param("idUsuario") Long idUsuario, @Param("estado") boolean estado);

}
