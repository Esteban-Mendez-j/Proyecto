package com.miproyecto.proyecto.repos;

import com.miproyecto.proyecto.domain.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;




public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoAndContrasena(String correo, String contrasena);
    
    Optional<Usuario> getByCorreo(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByTelefonoIgnoreCase(String telefono);

}
