package com.miproyecto.proyecto.model;

public class VacanteResumenDTO {
    private Long nvacantes;
    private String titulo;
    private String ciudad;
    private String tipo;
    private Boolean activaPorEmpresa;
    private Boolean isActive;
    
    public Boolean getActivaPorEmpresa() {
        return activaPorEmpresa;
    }
    public void setActivaPorEmpresa(Boolean activaPorEmpresa) {
        this.activaPorEmpresa = activaPorEmpresa;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public Long getId() {
        return nvacantes;
    }
    public void setId(Long nvacantes) {
        this.nvacantes = nvacantes;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}