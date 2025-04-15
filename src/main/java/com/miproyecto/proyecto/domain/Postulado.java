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
    private Vacante nvacante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_id")
    private Candidato idUsuario;

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

    public Vacante getNvacante() {
        return nvacante;
    }

    public void setNvacante(final Vacante nvacante) {
        this.nvacante = nvacante;
    }

    public Candidato getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(final Candidato idUsuario) {
        this.idUsuario = idUsuario;
    }

}
