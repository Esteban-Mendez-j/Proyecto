package com.miproyecto.proyecto.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.miproyecto.proyecto.domain.Roles;



public interface RolesRepository extends JpaRepository<Roles, Long> {
    Roles findByRol(String rol);

} 
