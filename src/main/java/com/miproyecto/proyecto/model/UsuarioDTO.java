package com.miproyecto.proyecto.model;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class UsuarioDTO {
    
    Long idUsuario;


    private List<String> roles;

    @NotNull
    @Size(max = 50)
    private String nombre;

    @NotNull(groups = ValidationGroups.OnCreate.class)
    @Size(max = 15, groups = ValidationGroups.OnCreate.class)
    private String contrasena;

    @NotNull
    @Size(max = 100)
    @UsuarioCorreoUnique(message = "Este correo ya esta registrado")
    private String correo;

    @Size(max = 15)
    @UsuarioTelefonoUnique(message = "Este telefono ya esta registrado")
    private String telefono;

    @Size(max = 400)
    private String descripcion;

    @NotNull(message = "Sube una imagen de perfil", groups = ValidationGroups.OnCreate.class)
    @Size(max = 255)
    private String imagen;

    private LocalDate fechaRegistro;

    private LocalDate fechaInicioSesion;

    private Boolean isActive;

    private String comentarioAdmin;
    
    
    // Constructor donde se inicializa el servicio de encriptación
    
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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
    
    public String getComentarioAdmin() {
        return comentarioAdmin;
    }

    public void setComentarioAdmin(String comentarioAdmin) {
        this.comentarioAdmin = comentarioAdmin;
    }
    

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
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

    public String getRolPrinciapl(){
        if (!roles.isEmpty()) {
            return roles.get(0); // o getAuthority()
        }
        return "ROLE_INVITADO";
    }
    

    @Override
    public String toString() {
        return "UsuarioDTO [idUsuario=" + idUsuario + ", roles=" + roles + ", nombre=" + nombre + ", correo=" + correo+"contraseña: "+ contrasena
                + "]";
    }


}
