package com.miproyecto.proyecto.model;

import java.time.LocalDate;

public class FiltroVacanteDTO {
    private String cargo;
    private LocalDate fechaPublicacion;
    private Double sueldo;
    private String modalidad;
    private String experiencia;
    private String ciudad;
    private String titulo;
    private String tipo;
    private String nameEmpresa;
    private Long idUsuario;
    private int totalpostulaciones;
    private String rolUser;
    private Boolean isActive;
    private Boolean activaPorEmpresa;

    public Boolean isActivaPorEmpresa() {
        return activaPorEmpresa;
    }
    public void setActivaPorEmpresa(Boolean activaPorEmpresa) {
        this.activaPorEmpresa = activaPorEmpresa;
    }
    public int getTotalpostulaciones() {
        return totalpostulaciones;
    }
    public void setTotalpostulaciones(int totalpostulaciones) {
        this.totalpostulaciones = totalpostulaciones;
    }

    public Boolean getIsActive() {
        return isActive;
    }
    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public String getRolUser() {
        return rolUser;
    }
    public void setRolUser(String rolUser) {
        this.rolUser = rolUser;
    }
    public Long getIdUsuario() {
        return idUsuario;
    }
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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
    public String getNameEmpresa() {
        return nameEmpresa;
    }
    public void setNameEmpresa(String nameEmpresa) {
        this.nameEmpresa = nameEmpresa;
    }
    @Override
    public String toString() {
        return "FiltroVacanteDTO [cargo=" + cargo + ", fechaPublicacion=" + fechaPublicacion + ", sueldo=" + sueldo
                + ", modalidad=" + modalidad + ", experiencia=" + experiencia + ", ciudad=" + ciudad + ", titulo="
                + titulo + ", tipo=" + tipo + ", nameEmpresa=" + nameEmpresa + ", idUsuario=" + idUsuario
                + ", totalpostulaciones=" + totalpostulaciones + ", rolUser=" + rolUser + ", isActive=" + isActive
                + ", activaPorEmpresa=" + activaPorEmpresa + "]\n";
    }

}