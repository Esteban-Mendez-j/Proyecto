package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Candidato;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    Optional<Candidato> findByIdentificacion( String identificacion);

    boolean existsByIdentificacionIgnoreCase(String identificacion);

    boolean existsByIdUsuario(Long idUsuario);


}
