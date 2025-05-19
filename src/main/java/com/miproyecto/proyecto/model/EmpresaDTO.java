package com.miproyecto.proyecto.model;

import jakarta.validation.constraints.Size;


public class EmpresaDTO extends UsuarioDTO  {

    @Size(max = 50)
    private String sectorEmpresarial;

    @Size(max = 255)
    private String sitioWeb;

    @Size(max = 10)
    @EmpresaNitUnique(message = "Este NIT ya esta registrado")
    private String nit;

    public String getSectorEmpresarial() {
        return sectorEmpresarial;
    }

    public void setSectorEmpresarial(final String sectorEmpresarial) {
        this.sectorEmpresarial = sectorEmpresarial;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(final String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(final String nit) {
        this.nit = nit;
    }

    @Override
    public String toString() {
        return "EmpresaDTO [sectorEmpresarial=" + sectorEmpresarial + ", sitioWeb=" + sitioWeb + ", nit=" + nit + "]";
    }


}
