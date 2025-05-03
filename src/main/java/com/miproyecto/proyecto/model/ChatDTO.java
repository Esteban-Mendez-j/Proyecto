package com.miproyecto.proyecto.model;

import java.time.LocalDateTime;

public class ChatDTO {

    private String id;
    private String empresaId;
    private String candidatoId;
    private Boolean isActive;
    private String ContentUltimoMensaje;
    private LocalDateTime horaUltimoMensaje;

    public LocalDateTime getHoraUltimoMensaje() {
        return horaUltimoMensaje;
    }
    public void setHoraUltimoMensaje(LocalDateTime horaUltimoMensaje) {
        this.horaUltimoMensaje = horaUltimoMensaje;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmpresaId() {
        return empresaId;
    }
    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }
    public String getCandidatoId() {
        return candidatoId;
    }
    public void setCandidatoId(String candidatoId) {
        this.candidatoId = candidatoId;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public String getContentUltimoMensaje() {
        return ContentUltimoMensaje;
    }
    public void setContentUltimoMensaje(String contentUltimoMensaje) {
        ContentUltimoMensaje = contentUltimoMensaje;
    }
}