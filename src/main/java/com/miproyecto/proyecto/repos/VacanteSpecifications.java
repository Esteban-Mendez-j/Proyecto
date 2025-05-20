package com.miproyecto.proyecto.repos;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.model.FiltroVacanteDTO;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class VacanteSpecifications {
    public static Specification<Vacante> conFiltros(FiltroVacanteDTO filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getIdUsuario() != null && filtro.getIdUsuario() > 0) {
                Join<Object, Object> empresaJoin = root.join("idUsuario", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(empresaJoin.get("idUsuario"), filtro.getIdUsuario()));   
            }

            if (filtro.getTotalpostulaciones() >= 0){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalpostulaciones"),filtro.getTotalpostulaciones()
                    )
                );
            }

            if (filtro.getNameEmpresa() != null && !filtro.getNameEmpresa().isEmpty()) {
                Join<Vacante, Empresa> empresaJoin = root.join("idUsuario", JoinType.INNER);
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(empresaJoin.get("nombre")),
                        "%" + filtro.getNameEmpresa().toLowerCase() + "%"));
            }
            
           
            if (filtro.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), filtro.getIsActive()));
            }

            if (filtro.isActivaPorEmpresa() != null) {
                predicates.add(criteriaBuilder.equal(root.get("activaPorEmpresa"), filtro.isActivaPorEmpresa()));
            }


            if (filtro.getTitulo() != null && !filtro.getTitulo().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), "%" + filtro.getTitulo().toLowerCase() + "%"));
            }

            if (filtro.getCargo() != null && !filtro.getCargo().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("cargo"), "%" + filtro.getCargo() + "%"));
            }

            if (filtro.getCiudad() != null && !filtro.getCiudad().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("ciudad"), filtro.getCiudad()));
            }

            if (filtro.getExperiencia() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("experiencia"), filtro.getExperiencia()));
            }

            if (filtro.getSueldo() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sueldo"), filtro.getSueldo()));
            }

            if (filtro.getFechaPublicacion() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaPublicacion"), filtro.getFechaPublicacion()));
            }

            if ("todos".equals(filtro.getTipo())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("tipo"), "Practica"),
                    criteriaBuilder.equal(root.get("tipo"), "Vacante")
                ));
            } else if (filtro.getTipo() != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipo"), filtro.getTipo()));
            }

            if ("null".equals(filtro.getModalidad())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("modalidad"), "Presencial"),
                    criteriaBuilder.equal(root.get("modalidad"), "Remota")
                ));
            } else if (filtro.getModalidad() != null) {
                predicates.add(criteriaBuilder.equal(root.get("modalidad"), filtro.getModalidad()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
