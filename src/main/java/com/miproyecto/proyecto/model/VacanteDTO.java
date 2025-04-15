package com.miproyecto.proyecto.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import com.miproyecto.proyecto.service.EncryptionService;


public class VacanteDTO {

    private String nvacantes;

    
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

   
    private String idUsuario;

    private String nameEmpresa;

    private String imagenEmpresa;
    
    

    private EncryptionService encryptionService;



    public VacanteDTO() {
        this.encryptionService = new EncryptionService(); 
    }

    public Long getNvacantes() {
        return encryptionService.decrypt(nvacantes);
    }

    public String getNvacantesEncryt(){
        return nvacantes;
    }

    public void setNvacantes(final Long nvacante) {
        this.nvacantes = encryptionService.encrypt(nvacante);
    }


    public String getCargo() {
        return cargo;
    }

    public void setCargo(final String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(final LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public Double getSueldo() {
        return sueldo;
    }

    public void setSueldo(final Double sueldo) {
        this.sueldo = sueldo;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(final String modalidad) {
        this.modalidad = modalidad;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(final String experiencia) {
        this.experiencia = experiencia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(final String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(final String departamento) {
        this.departamento = departamento;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(final String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRequerimientos() {
        return requerimientos;
    }

    public void setRequerimientos(String requerimientos) {
        this.requerimientos = requerimientos;
    }

    public String getNameEmpresa() {
        return nameEmpresa;
    }

    public void setNameEmpresa(String nameEmpresa) {
        this.nameEmpresa = nameEmpresa;
    }


    public Long getIdUsuario() {
        // Desencriptar el valor y convertirlo a Long
        return encryptionService.decrypt(idUsuario);
    }

    public String getIdUsuarioEncrypt() {
        return idUsuario;
    }
    
    public void setIdUsuario(final Long idUsuario) {
        // Encriptar el valor antes de almacenarlo como String
        this.idUsuario = encryptionService.encrypt(idUsuario);
    }

    public String getImagenEmpresa() {
        return imagenEmpresa;
    }

    public void setImagenEmpresa(String imagenEmpresa) {
        this.imagenEmpresa = imagenEmpresa;
    }

    @Override
    public String toString() {
        return "VacanteDTO [nvacantes=" + nvacantes + ", cargo=" + cargo + ", fechaPublicacion=" + fechaPublicacion
                + ", sueldo=" + sueldo + ", modalidad=" + modalidad + ", experiencia=" + experiencia + ", ciudad="
                + ciudad + ", departamento=" + departamento + ", titulo=" + titulo + ", tipo=" + tipo + ", descripcion="
                + descripcion + ", idUsuario=" + idUsuario + "]";
    }


}
