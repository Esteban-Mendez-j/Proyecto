package com.miproyecto.proyecto.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miproyecto.proyecto.domain.Roles;
import com.miproyecto.proyecto.domain.Usuario;
import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.repos.RolesRepository;
import com.miproyecto.proyecto.repos.UsuarioRepository;
import com.miproyecto.proyecto.repos.VacanteRepository;
import com.miproyecto.proyecto.util.NotFoundException;

@Service
@Transactional
public class AdminService {
    
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private VacanteRepository vacanteRepository;


    public void modificarRoles (Long idUsuario, boolean addRole ){
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(NotFoundException::new);

        Roles adminRole = rolesRepository.findByRol("ADMIN");
        if (usuario.getRoles().contains(adminRole) && addRole) {
            System.out.println("ya tiene el rol administardor"); // hay que pasar elk mensaje a la vista
            return;
        }
   
        if (addRole) {
            usuario.getRoles().add(adminRole);
        } else {
            usuario.getRoles().remove(adminRole);
        }
        usuarioRepository.save(usuario);
    }


    public void cambiarIsActive(Long idUsuario, boolean IsActive, String comentario){
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(NotFoundException::new);

        usuario.setIsActive(IsActive);
        usuario.setComentarioAdmin(comentario);
        usuarioRepository.save(usuario);
    }

    public void cambiarEstadoVacantes (Long Nvacante, String estado, String comentario ){
        Vacante vacante = vacanteRepository.findById(Nvacante)
            .orElseThrow(NotFoundException::new);
        if(!estado.equalsIgnoreCase(vacante.getEstado())){
            vacante.setEstado(estado);
            vacante.setComentarioAdmin(comentario);
            vacanteRepository.save(vacante);
        }
        
    }
}
