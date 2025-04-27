package com.miproyecto.proyecto.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

import java.util.Set;


@Entity
@PrimaryKeyJoinColumn(name = "id_candidato")
public class Candidato extends Usuario {

    @Column(length = 20)
    private String apellido;

    @Column
    private String curriculo;

    @Column(length = 4)
    private String experiencia;

    @Column(unique = true, length = 11)
    private String identificacion;

    @OneToMany(mappedBy = "candidato")
    private Set<Postulado> listarPostulados;

    @OneToMany(mappedBy = "idUsuario")
    private Set<Estudio> listarEstudios;

    @OneToMany(mappedBy = "idUsuario")
    private Set<HistorialLaboral> listarHistorial;

    public String getApellido() {
        return apellido;
    }

    public void setApellido(final String apellido) {
        this.apellido = apellido;
    }

    public String getCurriculo() {
        return curriculo;
    }

    public void setCurriculo(final String curriculo) {
        this.curriculo = curriculo;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(final String experiencia) {
        this.experiencia = experiencia;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(final String identificacion) {
        this.identificacion = identificacion;
    }

    public Set<Postulado> getListarPostulados() {
        return listarPostulados;
    }

    public void setListarPostulados(final Set<Postulado> listarPostulados) {
        this.listarPostulados = listarPostulados;
    }

    public Set<Estudio> getListarEstudios() {
        return listarEstudios;
    }

    public void setListarEstudios(final Set<Estudio> listarEstudios) {
        this.listarEstudios = listarEstudios;
    }

    public Set<HistorialLaboral> getListarHistorial() {
        return listarHistorial;
    }

    public void setListarHistorial(final Set<HistorialLaboral> listarHistorial) {
        this.listarHistorial = listarHistorial;
    }

}
