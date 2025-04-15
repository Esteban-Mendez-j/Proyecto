package com.miproyecto.proyecto.model;

import com.miproyecto.proyecto.service.EncryptionService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class EstudioDTO {

    private String idEstudio;

    @NotNull
    @Size(max = 80)
    private String titulo;

    @NotNull
    @Size(max = 80)
    private String academia;

    private String idUsuario;


    private EncryptionService encryptionService;

    public EstudioDTO() {
        this.encryptionService = new EncryptionService();  
    }

    public Long getIdEstudio() {
        return encryptionService.decrypt(idEstudio);
    }

    public void setIdEstudio(final Long idEstudio) {
        this.idEstudio = encryptionService.encrypt(idEstudio);
    }

    public String getIdEstudioEncrypt() {
        return idEstudio;
    }
    

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public String getAcademia() {
        return academia;
    }

    public void setAcademia(final String academia) {
        this.academia = academia;
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
