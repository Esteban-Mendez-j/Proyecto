package com.miproyecto.proyecto.model;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class EstudioDTO {

    private Long idEstudio;

    @NotNull
    @Size(max = 80)
    private String titulo;

    @NotNull
    @Size(max = 80)
    private String academia;

    private Long idUsuario;

    public Long getIdEstudio() {
        return idEstudio;
    }

    public void setIdEstudio(Long idEstudio) {
        this.idEstudio = idEstudio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAcademia() {
        return academia;
    }

    public void setAcademia(String academia) {
        this.academia = academia;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "EstudioDTO [idEstudio=" + idEstudio + ", titulo=" + titulo + ", academia=" + academia + ", idUsuario="
                + idUsuario + "]";
    }


}
