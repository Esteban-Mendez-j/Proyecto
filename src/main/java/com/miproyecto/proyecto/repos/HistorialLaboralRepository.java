package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.HistorialLaboral;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface HistorialLaboralRepository extends JpaRepository<HistorialLaboral, Long> {

    HistorialLaboral findFirstByIdUsuario(Candidato candidato);

    List<HistorialLaboral> findByIdUsuario(Candidato candidato);

    void deleteByIdUsuario_IdUsuario(Long idUsuario);

}
