package com.miproyecto.proyecto.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;


@Entity
public class Postulado {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nPostulacion;

    @Column(nullable = false)
    private LocalDate fechaPostulacion;

    @Column(nullable = false, length = 10)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nvacante_id", nullable = false)
    private Vacante vacante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_id")
    private Candidato candidato;

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

    public Long getNPostulacion() {
        return nPostulacion;
    }

    public void setNPostulacion(final Long nPostulacion) {
        this.nPostulacion = nPostulacion;
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

    public Vacante getVacante() {
        return vacante;
    }

    public void setVacante(final Vacante vacante) {
        this.vacante = vacante;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(final Candidato candidato) {
        this.candidato = candidato;
    }

}
