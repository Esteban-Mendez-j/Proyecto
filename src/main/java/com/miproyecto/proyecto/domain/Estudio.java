package com.miproyecto.proyecto.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
public class Estudio {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEstudio;

    @Column(nullable = false, length = 80)
    private String titulo;

    @Column(nullable = false, length = 80)
    private String academia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_id")
    private Candidato idUsuario;

    public Long getIdEstudio() {
        return idEstudio;
    }

    public void setIdEstudio(final Long idEstudio) {
        this.idEstudio = idEstudio;
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

    public Candidato getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(final Candidato idUsuario) {
        this.idUsuario = idUsuario;
    }

}
