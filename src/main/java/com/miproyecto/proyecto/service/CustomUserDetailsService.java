package com.miproyecto.proyecto.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.miproyecto.proyecto.domain.Usuario;
import com.miproyecto.proyecto.util.NotFoundException;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioService usuarioService;

    public CustomUserDetailsService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        try {
            if (correo == null || correo.isBlank()) {
                throw new UsernameNotFoundException("Correo no puede ser nulo o vacío");
            }
            
            Usuario usuario = usuarioService.findByCorreo(correo, true).orElseThrow(() -> {
                return new UsernameNotFoundException("Usuario no encontrado");
            });
            
            
            return new User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                usuario.getIsActive(), 
                true,           
                true,       
                true,            
                usuario.getRoles().stream()
                    .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getRol()))
                    .collect(Collectors.toList())
            );
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
            

    }
}