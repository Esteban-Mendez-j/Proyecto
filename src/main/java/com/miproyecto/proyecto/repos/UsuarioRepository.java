package com.miproyecto.proyecto.repos;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.miproyecto.proyecto.domain.Usuario;





public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findByCorreoAndContrasena(String correo, String contrasena);
    
    Optional<Usuario> getByCorreo(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByTelefonoIgnoreCase(String telefono);

    Page<Usuario> findByIsActive(boolean isActive, Pageable pageable);
    
    List<Usuario> findIsActiveUsuariosByIsActive(Boolean isActive, Pageable pageable);

}
