package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.domain.Vacante;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface VacanteRepository extends JpaRepository<Vacante, Long>, JpaSpecificationExecutor<Vacante> {

    List<Vacante> findByIdUsuario(Empresa empresa);

    Vacante findFirstByIdUsuario(Empresa empresa);

    boolean existsById(Long idUsuario);

    

}
