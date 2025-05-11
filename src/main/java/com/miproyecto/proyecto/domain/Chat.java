package com.miproyecto.proyecto.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chats")
public class Chat {

    @Id
    private String id;
    private String empresaId;
    private String nombreEmpresa;
    private String candidatoId;
    private String nombreCandidato;
    private LocalDateTime createdAt;
    private LocalDateTime horaUltimoMensaje;
    private String ContentUltimoMensaje; 
    private String tituloVacante;
    private Boolean isActive;
    private String vacanteId;

    public String getTituloVacante() {
        return tituloVacante;
    }
    public void setTituloVacante(String tituloVacante) {
        this.tituloVacante = tituloVacante;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }
    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getNombreCandidato() {
        return nombreCandidato;
    }
    public void setNombreCandidato(String nombreCandidato) {
        this.nombreCandidato = nombreCandidato;
    }

    public String getVacanteId() {
        return vacanteId;
    }
    public void setVacanteId(String vacanteId) {
        this.vacanteId = vacanteId;
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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getHoraUltimoMensaje() {
        return horaUltimoMensaje;
    }
    public void setHoraUltimoMensaje(LocalDateTime horaUltimoMensaje) {
        this.horaUltimoMensaje = horaUltimoMensaje;
    }
    public String getContentUltimoMensaje() {
        return ContentUltimoMensaje;
    }
    public void setContentUltimoMensaje(String contentUltimoMensaje) {
        ContentUltimoMensaje = contentUltimoMensaje;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}