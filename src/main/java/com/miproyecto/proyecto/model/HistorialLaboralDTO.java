package com.miproyecto.proyecto.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class HistorialLaboralDTO {

    @JsonProperty("iDHistorial")
    private Long iDHistorial;

    @NotNull
    @Size(max = 100)
    private String titulo;

    @NotNull
    @Size(max = 100)
    private String empresa;

    private Long idUsuario;

    public Long getiDHistorial() {
        return iDHistorial;
    }

    public void setiDHistorial(Long iDHistorial) {
        this.iDHistorial = iDHistorial;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "HistorialLaboralDTO [iDHistorial=" + iDHistorial + ", titulo=" + titulo + ", empresa=" + empresa
                + ", idUsuario=" + idUsuario + "]";
    }

  

}