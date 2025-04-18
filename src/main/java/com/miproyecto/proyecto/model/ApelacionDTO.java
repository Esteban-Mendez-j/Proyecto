package com.miproyecto.proyecto.model;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class ApelacionDTO {

    private Long id;

    @NotNull
    private String mensaje;
    
    private String estado; //  "PENDIENTE", "APROBADA", "RECHAZADA"

    private Boolean esPorCuenta; // true = apelación por cuenta, false = por vacante

    private String comentarioAdmin;

    private LocalDateTime fechaCreacion;

    private Long idUsuario; 

    private Long idVacante;

    private Long idAdmin; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getEsPorCuenta() {
        return esPorCuenta;
    }

    public void setEsPorCuenta(Boolean esPorCuenta) {
        this.esPorCuenta = esPorCuenta;
    }

    public String getComentarioAdmin() {
        return comentarioAdmin;
    }

    public void setComentarioAdmin(String comentarioAdmin) {
        this.comentarioAdmin = comentarioAdmin;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdVacante() {
        return idVacante;
    }

    public void setIdVacante(Long idVacante) {
        this.idVacante = idVacante;
    }

    public Long getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(Long idAdmin) {
        this.idAdmin = idAdmin;
    }

}