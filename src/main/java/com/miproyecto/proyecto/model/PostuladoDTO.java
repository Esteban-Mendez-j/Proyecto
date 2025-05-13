package com.miproyecto.proyecto.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;



public class PostuladoDTO {

    @JsonProperty("nPostulacion")
    private Long nPostulacion;

    @NotNull
    private LocalDate fechaPostulacion;

    @NotNull
    @Size(max = 10)
    private String estado;

    @NotNull
    private VacanteResumenDTO vacante;

    private CandidatoResumenDTO candidato;

    private boolean isActive;

    private boolean vacanteIsActive; 

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean isVacanteIsActive() {
        return vacanteIsActive;
    }

    public void setVacanteIsActive(boolean vacanteIsActive) {
        this.vacanteIsActive = vacanteIsActive;
    }

    public Long getnPostulacion() {
        return nPostulacion;
    }

    public void setnPostulacion(Long nPostulacion) {
        this.nPostulacion = nPostulacion;
    }

    public LocalDate getFechaPostulacion() {
        return fechaPostulacion;
    }

    public void setFechaPostulacion(LocalDate fechaPostulacion) {
        this.fechaPostulacion = fechaPostulacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public VacanteResumenDTO getVacante() {
        return vacante;
    }

    public void setVacante(VacanteResumenDTO vacante) {
        this.vacante = vacante;
    }

    public CandidatoResumenDTO getCandidato() {
        return candidato;
    }

    public void setCandidato(CandidatoResumenDTO candidato) {
        this.candidato = candidato;
    } 

    
}
