package com.miproyecto.proyecto.model;


import java.util.List;

import com.miproyecto.proyecto.domain.Roles;
import com.miproyecto.proyecto.service.EncryptionService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class UsuarioDTO {
    
    String idUsuario;

    private List<Roles> roles;

    @NotNull
    @Size(max = 50)
    private String nombre;

    @NotNull(groups = ValidationGroups.OnCreate.class)
    @Size(max = 15)
    private String contrasena;

    @NotNull
    @Size(max = 100)
    @UsuarioCorreoUnique(message = " ya está registrado")
    private String correo;

    @Size(max = 15)
    @UsuarioTelefonoUnique(message = " ya está registrado")
    private String telefono;

    @Size(max = 400)
    private String descripcion;

    @Size(max = 255)
    private String imagen;

    private EncryptionService encryptionService;

    // Constructor donde se inicializa el servicio de encriptación
    public UsuarioDTO() {
        this.encryptionService = new EncryptionService();  
    }

    public Long getIdUsuario() {
        return encryptionService.decrypt(idUsuario);
    }

    public String getIdUsuarioEncrypt() {
        return idUsuario;
    }
    
    public void setIdUsuario(final Long idUsuario) {
        this.idUsuario = encryptionService.encrypt(idUsuario);
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

    @Override
    public String toString() {
        return "UsuarioDTO [idUsuario=" + idUsuario + ", roles=" + roles + ", nombre=" + nombre + ", contrasena="
                + contrasena + ", correo=" + correo + ", telefono=" + telefono + ", descripcion=" + descripcion
                + ", imagen=" + imagen + ", encryptionService=" + encryptionService + "]";
    }

}
