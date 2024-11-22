package com.miproyecto.proyecto.repos;
import org.springframework.data.jpa.domain.Specification;
 

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;


import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.model.VacanteDTO;

public class VacanteSpecifications {
    
    public static Specification<Vacante> conFiltros(VacanteDTO filtro) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro para título
            if (filtro.getTitulo() != null && !filtro.getTitulo().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), "%" + filtro.getTitulo().toLowerCase() + "%"));
            }
        
            // Filtro para cargo
            if (filtro.getCargo() != null && !filtro.getCargo().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("cargo"), "%" + filtro.getCargo() + "%"));
            }
    
            // Filtro para ciudad
            if (filtro.getCiudad() != null && !filtro.getCiudad().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("ciudad"), filtro.getCiudad()));
            }
    
            // Filtro para experiencia mínima (valores iguales o superiores)
            if (filtro.getExperiencia() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("experiencia"), filtro.getExperiencia()));
            }
    
            // Filtro para sueldo mínimo
            if (filtro.getSueldo() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("sueldo"), filtro.getSueldo()));
            }
    
            // Filtro para fecha de publicación (fechas iguales o posteriores)
            if (filtro.getFechaPublicacion() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaPublicacion"), filtro.getFechaPublicacion()));
            }
    
            // Filtro para tipo de vacante (Voluntariado o Vacante)

            if ("null".equals(filtro.getTipo())) {
                System.out.println("");
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("tipo"), "Voluntariado"),
                    criteriaBuilder.equal(root.get("tipo"), "Vacante")
                ));
            }else {
                predicates.add(criteriaBuilder.equal(root.get("tipo"), filtro.getTipo()));   
            }
    
            // Filtro para modalidad (Presencial o Remota)
            if ("null".equals(filtro.getModalidad())) {
                predicates.add(criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("modalidad"), "Presencial"),
                    criteriaBuilder.equal(root.get("modalidad"), "Remota")
                ));
            } else {
                predicates.add(criteriaBuilder.equal(root.get("modalidad"), filtro.getModalidad()));
            }

            // Combinamos todos los predicados con "AND"
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
