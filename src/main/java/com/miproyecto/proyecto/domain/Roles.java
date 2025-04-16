package com.miproyecto.proyecto.domain;

import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;


@Entity
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id_rol;

    @Column
    private String rol;

    
    @ManyToMany(mappedBy = "roles")
    private List<Usuario> usuarios;


    public Roles() {
    }

    public Long getId_rol() {
        return Id_rol;
    }

    public void setId_rol(Long id_rol) {
        Id_rol = id_rol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

}
