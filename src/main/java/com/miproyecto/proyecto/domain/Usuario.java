package com.miproyecto.proyecto.domain;

import java.time.LocalDate;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long idUsuario;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(unique = true, length = 15)
    private String telefono;

    @Column(length = 400)
    private String descripcion;

    @Column
    private String imagen;

    @Column
    private Boolean isActive;

    @Column
    private String comentarioAdmin;

    @Column
    private LocalDate fechaRegistro;

    @Column
    private LocalDate fechaInicioSesion;
    
    @ManyToMany(fetch = FetchType.EAGER , cascade = CascadeType.MERGE )
    @JoinTable(
        name = "usuario_Rol",
        joinColumns = @JoinColumn(name = "idUsuario"),
        inverseJoinColumns = @JoinColumn(name = "Id_rol")
        )
    @OrderColumn(name = "orden_rol") 
    private List<Roles> roles;

    // Relacion con las apelaciones en las que el Usuario es el candidato o empresa
    @OneToMany(mappedBy = "usuario")
    private List<Apelacion> apelacionesComoUsuario;

    // Relacion con las apelaciones en las que el Usuario es admin
    @OneToMany(mappedBy = "admin")
    private List<Apelacion> apelacionesComoAdmin;

        
    public Usuario() {
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDate getFechaInicioSesion() {
        return fechaInicioSesion;
    }

    public void setFechaInicioSesion(LocalDate fechaInicioSesion) {
        this.fechaInicioSesion = fechaInicioSesion;
    }
    
    public Long getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(final Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<Roles> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(final String contrasena) {
        this.contrasena = contrasena;
    }
    
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(final String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(final String telefono) {
        this.telefono = telefono;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getImagen() {
        return imagen;
    }
    
    public void setImagen(final String imagen) {
        this.imagen = imagen;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getComentarioAdmin() {
        return comentarioAdmin;
    }

    public void setComentarioAdmin(String comentarioAdmin) {
        this.comentarioAdmin = comentarioAdmin;
    }
    
}
