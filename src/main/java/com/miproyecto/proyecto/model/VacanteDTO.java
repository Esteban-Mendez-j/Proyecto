package com.miproyecto.proyecto.model;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class VacanteDTO {

    private Long nvacantes;

    @NotNull
    @Size(max = 50)
    private String cargo;

    private LocalDate fechaPublicacion;

    private Double sueldo;

    @NotNull
    @Size(max = 50)
    private String modalidad;

    @Size(max = 4)
    private String experiencia;

    @NotNull
    @Size(max = 50)
    private String ciudad;

    @NotNull
    @Size(max = 50)
    private String departamento;

    @NotNull
    @Size(max = 100)
    private String titulo;

    @NotNull
    @Size(max = 15)
    private String tipo;

    @Size(max = 400)
    private String descripcion;

    @Size(max = 400)
    private String requerimientos;
    
    private int totalpostulaciones;

    private Boolean candidatoPostulado;

    private Boolean activaPorEmpresa;

    private Boolean isActive;

    private String comentarioAdmin;

    private Long idUsuario;

    private String nameEmpresa;

    private String imagenEmpresa;

    private int nPostulados;

    private String estadoPostulacion;


    public String getEstadoPostulacion() {
        return estadoPostulacion;
    }

    public void setEstadoPostulacion(String estadoPostulacion) {
        this.estadoPostulacion = estadoPostulacion;
    }

    public int getnPostulados() {
        return nPostulados;
    }

    public Boolean getCandidatoPostulado() {
        return candidatoPostulado;
    }

    public Boolean isActivaPorEmpresa() {
        return activaPorEmpresa;
    }

    public void setActivaPorEmpresa(Boolean activaPorEmpresa) {
        this.activaPorEmpresa = activaPorEmpresa;
    }

    public void setCandidatoPostulado(Boolean candidatoPostulado) {
        this.candidatoPostulado = candidatoPostulado;
    }

    public int getTotalpostulaciones() {
        return totalpostulaciones;
    }

    public void setTotalpostulaciones(int totalpostulaciones) {
        this.totalpostulaciones = totalpostulaciones;
    }

    public void setnPostulados(int nPostulados) {
        this.nPostulados = nPostulados;
    }

    public Long getNvacantes() {
        return nvacantes;
    }

    public void setNvacantes(Long nvacantes) {
        this.nvacantes = nvacantes;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Double getSueldo() {
        return sueldo;
    }

    public void setSueldo(Double sueldo) {
        this.sueldo = sueldo;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRequerimientos() {
        return requerimientos;
    }

    public void setRequerimientos(String requerimientos) {
        this.requerimientos = requerimientos;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getComentarioAdmin() {
        return comentarioAdmin;
    }

    public void setComentarioAdmin(String comentarioAdmin) {
        this.comentarioAdmin = comentarioAdmin;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNameEmpresa() {
        return nameEmpresa;
    }

    public void setNameEmpresa(String nameEmpresa) {
        this.nameEmpresa = nameEmpresa;
    }

    public String getImagenEmpresa() {
        return imagenEmpresa;
    }

    public void setImagenEmpresa(String imagenEmpresa) {
        this.imagenEmpresa = imagenEmpresa;
    }
}
