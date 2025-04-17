package com.miproyecto.proyecto.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.miproyecto.proyecto.domain.Roles;
import com.miproyecto.proyecto.repos.RolesRepository;

import java.util.List;

@Component
public class InyeccionRoles implements CommandLineRunner {

    private final RolesRepository rolesRepository;

    public InyeccionRoles(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        List<String> roles = List.of("CANDIDATO", "EMPRESA", "ADMIN", "SUPER_ADMIN");

        for (String nombreRol : roles) {
            if (rolesRepository.findByRol(nombreRol) == null) {
                Roles rol = new Roles();
                rol.setRol(nombreRol);
                rolesRepository.save(rol);
            }
        }
    }
}
