package com.miproyecto.proyecto.model;

import java.time.LocalDateTime;

public class ChatDTO {

    private String id;
    private String empresaId;
    private String nombreEmpresa;
    private String candidatoId;
    private String nombreCandidato;
    private String vacanteId;
    private Boolean isActive;
    private String tituloVacante;
    private String ContentUltimoMensaje;
    private LocalDateTime horaUltimoMensaje;

    public String getTituloVacante() {
        return tituloVacante;
    }
    public void setTituloVacante(String tituloVacante) {
        this.tituloVacante = tituloVacante;
    }
    public String getNombreCandidato() {
        return nombreCandidato;
    }
    public void setNombreCandidato(String nombreCandidato) {
        this.nombreCandidato = nombreCandidato;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }
    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getVacanteId() {
        return vacanteId;
    }
    public void setVacanteId(String vacanteId) {
        this.vacanteId = vacanteId;
    }

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