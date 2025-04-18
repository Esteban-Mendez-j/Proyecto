package com.miproyecto.proyecto.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.miproyecto.proyecto.domain.Apelacion;

public interface ApelacionRepository extends JpaRepository<Apelacion, Long > {

    // Buscar por estado, ignorando mayúsculas y minúsculas
    List<Apelacion> findByEstadoIgnoreCase(String estado);

    // Buscar apelaciones por el ID del usuario que las creó
    List<Apelacion> findByUsuario_IdUsuario(Long idUsuario);

    // Buscar por si la apelación es por cuenta o por vacante
    List<Apelacion> findByEsPorCuenta(Boolean esPorCuenta);

    // (Opcional) Buscar por el ID del admin que respondió
    List<Apelacion> findByAdmin_IdUsuario(Long idAdmin);
    
} 