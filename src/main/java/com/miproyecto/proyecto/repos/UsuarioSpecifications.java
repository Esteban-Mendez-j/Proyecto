package com.miproyecto.proyecto.repos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.miproyecto.proyecto.domain.Roles;
//  import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.domain.Usuario;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class UsuarioSpecifications {

        public static Specification<Usuario> conFiltros( String nombre, String rol, Boolean estado) {
                return (root, query, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        System.out.println("Este es el rol: " + rol);
                       
                        // if(idUsuario != null){
                        // predicates.add(criteriaBuilder.equal(root.get("idUsuario"),    idUsuario.n));
                        //   //falta arreglar xd

                        // }       

                        // Filtro por nombre
                        if (nombre != null && !nombre.isEmpty()) {
                                predicates.add(criteriaBuilder.like(
                                                criteriaBuilder.lower(root.get("nombre")),
                                                "%" + nombre.toLowerCase() + "%"));

                        }
                        // Filtro por Actividad
                        if (estado != null) {
                                predicates.add(criteriaBuilder.equal(root.get("isActive"), estado));

                        }
                        if (rol != null && !rol.isEmpty()) {
                                Join<Usuario, Roles> joinRoles = root.join("roles", JoinType.INNER);
                                predicates.add(criteriaBuilder.like(
                                                criteriaBuilder.lower(joinRoles.get("rol")),
                                                "%" + rol.toLowerCase() + "%"));

                                
                        };
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

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