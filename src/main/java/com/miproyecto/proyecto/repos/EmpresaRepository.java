package com.miproyecto.proyecto.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Empresa;


public interface EmpresaRepository extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {

    
    Candidato findByIdUsuario(Long idUsuario);

    boolean existsByNitIgnoreCase(String nit);

    boolean existsByIdUsuario(Long idUsuario);

}
