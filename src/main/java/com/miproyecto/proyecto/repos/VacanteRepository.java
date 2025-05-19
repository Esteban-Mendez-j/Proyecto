package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.domain.Vacante;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface VacanteRepository extends JpaRepository<Vacante, Long>, JpaSpecificationExecutor<Vacante>{

    Page<Vacante> findByIdUsuario(Empresa empresa, Pageable pageable);

    Vacante findFirstByIdUsuario(Empresa empresa);

    Optional<Vacante> findByIdUsuarioAndNvacantes(Empresa idUsuario, Long nvacantes);

    Page<Vacante> findByIsActiveOrderByFechaPublicacionDesc(Boolean estado, Pageable pageable);

    List<Vacante> findTop2ByIsActiveOrderByFechaPublicacionDesc(Boolean estado);

    List<Vacante> findTop2ByIsActiveOrderBySueldoDesc(Boolean estado);

    List<Vacante> findTop2ByIsActiveOrderByExperienciaAsc(Boolean estado);

    List<Vacante> findTop6ByIdUsuarioOrderByTotalpostulacionesDesc(Empresa idUsuario);
    
}
