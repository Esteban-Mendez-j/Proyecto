package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Estudio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface EstudioRepository extends JpaRepository<Estudio, Long> {

    Estudio findFirstByIdUsuario(Candidato candidato);

    boolean existsByIdUsuario(Candidato candidato);

    List<Estudio> findByIdUsuario(Candidato candidato);

    void deleteByIdUsuario_IdUsuario(Long idUsuario);
    

}
