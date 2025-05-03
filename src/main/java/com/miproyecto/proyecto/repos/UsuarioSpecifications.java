package com.miproyecto.proyecto.repos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

//  import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.domain.Usuario;
//  import com.miproyecto.proyecto.model.EmpresaDTO;
import com.miproyecto.proyecto.model.UsuarioDTO;

import jakarta.persistence.criteria.Predicate;

public class UsuarioSpecifications {

        public static Specification<Usuario> conFiltros(UsuarioDTO filtro){
                return(root, query, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();

                        // Filtro por nombre
                        if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
                                predicates.add(criteriaBuilder.like(
                                                criteriaBuilder.lower(root.get("nombre")),
                                                "%" + filtro.getNombre().toLowerCase() + "%"));

                        }       
                        // Filtro por Actividad
                        if (filtro.getIsActive() != null && !filtro.getIsActive()){
                                predicates.add(criteriaBuilder.like(
                                                criteriaBuilder.lower(root.get("actividad")),
                                                "%" + filtro.getIsActive() + "%"));

                        }
                        if (filtro.getRoles() != null && !filtro.getRoles().isEmpty()){
                                predicates.add(criteriaBuilder.like(
                                                criteriaBuilder.lower(root.get("actividad")),
                                                "%" + filtro.getRoles() + "%"));
                     
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                };
                return null;



                };
        }
}
 

// public static Specification<Empresa> conFiltros(EmpresaDTO filtro) {
// return (root, query, criteriaBuilder) -> {
// List<Predicate> predicates = new ArrayList<>();

// // Filtro por sectorEmpresarial
// if (filtro.getSectorEmpresarial() != null &&
// !filtro.getSectorEmpresarial().isEmpty()) {
// predicates.add(criteriaBuilder.like(
// criteriaBuilder.lower(root.get("sectorEmpresarial")),
// "%" + filtro.getSectorEmpresarial().toLowerCase() + "%"
// ));
// }

// // Combinar todos los predicados con AND
// return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
// }

// public static Specification<Usuario> filtroUsuario(UsuarioDTO filtro) {
// return (root, query, criteriaBuilder) -> {
// List<Predicate> predicates = new ArrayList<>();

// if (filtro.getNombre() != null && !filtro.getNombre().isEmpty()) {
// predicates.add(criteriaBuilder.like(
// criteriaBuilder.lower(usuarioJoin.get("nombre")),
// "%" + filtro.getNombre().toLowerCase() + "%"
// ));
// }
// return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

// // JOIN con usuario
// Join<Object, Object> usuarioJoin = root.join("usuario");

// Filtro por nombre del usuario (que representa el nombre de la empresa)

// }; }
// }

// }