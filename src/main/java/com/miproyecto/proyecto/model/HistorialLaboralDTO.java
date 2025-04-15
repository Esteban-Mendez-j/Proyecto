package com.miproyecto.proyecto.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.miproyecto.proyecto.service.EncryptionService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class HistorialLaboralDTO {

    @JsonProperty("iDHistorial")
    private String iDHistorial;

    @NotNull
    @Size(max = 100)
    private String titulo;

    @NotNull
    @Size(max = 100)
    private String empresa;

    private String idUsuario;

    private EncryptionService encryptionService;

   
    public HistorialLaboralDTO() {
        this.encryptionService = new EncryptionService();  
    }

    public Long getIDHistorial() {
        return encryptionService.decrypt(iDHistorial);
    }

    public String getIdHistorialEncrypt() {
        return iDHistorial;
    }

    public void setIDHistorial(final Long iDHistorial) {
        this.iDHistorial = encryptionService.encrypt(iDHistorial);
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(final String empresa) {
        this.empresa = empresa;
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
