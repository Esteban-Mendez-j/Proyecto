package com.miproyecto.proyecto.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class CandidatoDTO  extends UsuarioDTO {

    @NotNull
    @Size(max = 20)
    private String apellido;

    @Size(max = 225)
    private String curriculo;

    @Size(max = 4)
    private String experiencia;

    @Size(max = 11)
    @CandidatoIdentificacionUnique(message = "Esta identificacion ya esta registrada")
    private String identificacion;


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

    @Override
    public String toString() {
        return "CandidatoDTO [apellido=" + apellido + ", curriculo=" + curriculo + ", experiencia=" + experiencia
                + ", identificacion=" + identificacion + ", toString()=" + super.toString() + "]";
    }

    
}
