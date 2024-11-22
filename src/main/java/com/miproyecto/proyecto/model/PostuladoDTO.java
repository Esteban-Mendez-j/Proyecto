package com.miproyecto.proyecto.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.miproyecto.proyecto.service.EncryptionService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;



public class PostuladoDTO {

    @JsonProperty("nPostulacion")
    private String nPostulacion;

    @NotNull
    private LocalDate fechaPostulacion;

    @NotNull
    @Size(max = 10)
    private String estado;

    @NotNull
    private String nvacante;

    private String idUsuario; 

    private EncryptionService encryptionService;

    // Constructor donde se inicializa el servicio de encriptación
    public PostuladoDTO() {
        this.encryptionService = new EncryptionService();  // Inicializa el servicio de encriptación
    }

    public Long getNPostulacion() {
        return encryptionService.decrypt(nPostulacion);
    }

    public String getNPostulacionEncrypt() {
        return nPostulacion;
    }
    

    public void setNPostulacion(final Long nPostulacion) {
        this.nPostulacion = encryptionService.encrypt(nPostulacion);
    }

    
    public LocalDate getFechaPostulacion() {
        return fechaPostulacion;
    }

    public void setFechaPostulacion(final LocalDate fechaPostulacion) {
        this.fechaPostulacion = fechaPostulacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(final String estado) {
        this.estado = estado;
    }

    public Long getNvacante() {
        return encryptionService.decrypt(nvacante);
    }

    public String getNvacanteEncrypt(){
        return nvacante;
    }

    public void setNvacante(final Long nvacante) {
        this.nvacante = encryptionService.encrypt(nvacante);
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
}
