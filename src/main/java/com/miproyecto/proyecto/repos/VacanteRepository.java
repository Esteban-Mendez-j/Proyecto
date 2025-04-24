package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.domain.Vacante;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface VacanteRepository extends JpaRepository<Vacante, Long>, JpaSpecificationExecutor<Vacante>{

    Page<Vacante> findByIdUsuario(Empresa empresa, Pageable pageable);

    Vacante findFirstByIdUsuario(Empresa empresa);

    Page<Vacante> findByEstadoOrderByFechaPublicacionDesc(String estado, Pageable pageable);

    List<Vacante> findTop3ByEstadoOrderByFechaPublicacionDesc(String estado);

    List<Vacante> findTop3ByEstadoOrderBySueldoDesc(String estado);

    List<Vacante> findTop3ByEstadoOrderByExperienciaAsc(String estado);

    // @Query("SELECT v FROM Vacante v LEFT JOIN v.litarpostulados p WHERE v.idUsuario = :idEmpresa AND v.estado = 'activa' GROUP BY v ORDER BY COUNT(p) DESC")
    // List<Vacante> findVacantesConMasPostulacionesPorEmpresa(@Param("idEmpresa") Long idEmpresa);

}
