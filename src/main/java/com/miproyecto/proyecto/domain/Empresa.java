package com.miproyecto.proyecto.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

import java.util.Set;


@Entity
@PrimaryKeyJoinColumn(name = "id_empresa")
public class Empresa  extends Usuario {

    @Column(length = 50)
    private String sectorEmpresarial;

    @Column
    private String sitioWeb;

    @Column(unique = true, length = 10)
    private String nit;

    @OneToMany(mappedBy = "idUsuario")
    private Set<Vacante> listarVacantes;

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

    public Set<Vacante> getListarVacantes() {
        return listarVacantes;
    }

    public void setListarVacantes(final Set<Vacante> listarVacantes) {
        this.listarVacantes = listarVacantes;
    }

}
