package com.miproyecto.proyecto.domain;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;


@Entity
public class Vacante {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nvacantes;

    @Column(nullable = false, length = 50)
    private String cargo;

    @Column(nullable = false)
    private LocalDate fechaPublicacion;

    @Column
    private Double sueldo;

    @Column(nullable = false, length = 50)
    private String modalidad;

    @Column
    private String experiencia;

    @Column(nullable = false, length = 50)
    private String ciudad;

    @Column(nullable = false, length = 50)
    private String departamento;


    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 15)
    private String tipo;

    @Column(length = 400)
    private String descripcion;

    @Column(length = 400)
    private String requerimientos;

    @Column
    private int totalpostulaciones;
   
    @Column
    private Boolean isActive;

    @Column
    private Boolean activaPorEmpresa;

    @Column
    private String comentarioAdmin;


    @OneToMany(mappedBy = "vacante")
    private Set<Postulado> litarpostulados;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_id")
    private Empresa idUsuario;

    public Long getNvacantes() {
        return nvacantes;
    }

    public Boolean isActivaPorEmpresa() {
        return activaPorEmpresa;
    }

    public void setActivaPorEmpresa(Boolean activaPorEmpresa) {
        this.activaPorEmpresa = activaPorEmpresa;
    }

    public void setNvacantes(final Long nvacantes) {
        this.nvacantes = nvacantes;
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

    public Set<Postulado> getLitarpostulados() {
        return litarpostulados;
    }

    public void setLitarpostulados(final Set<Postulado> litarpostulados) {
        this.litarpostulados = litarpostulados;
    }

    public Empresa getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(final Empresa idUsuario) {
        this.idUsuario = idUsuario;
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

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getComentarioAdmin() {
        return comentarioAdmin;
    }

    public void setComentarioAdmin(String comentarioAdmin) {
        this.comentarioAdmin = comentarioAdmin;
    }

}
