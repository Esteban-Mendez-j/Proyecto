package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    
    Candidato findByIdUsuario(Long idUsuario);

    boolean existsByNitIgnoreCase(String nit);

    boolean existsByIdUsuario(Long idUsuario);

}
