package com.miproyecto.proyecto.repos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.miproyecto.proyecto.domain.Roles;
import com.miproyecto.proyecto.domain.Usuario;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class UsuarioSpecifications {

    public static Specification<Usuario> conFiltros(String rolUsuario, String nombre, String rol, Boolean estado) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por nombre
            if (nombre != null && !nombre.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("nombre")),
                    "%" + nombre.toLowerCase() + "%"));
            }

            // Filtro por estado de actividad
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), estado));
            }

            // Join con la tabla de roles
            Join<Usuario, Roles> joinRoles = root.join("roles", JoinType.INNER);

            // Lógica para SUPER_ADMIN
            if ("SUPER_ADMIN".equalsIgnoreCase(rolUsuario)) {
                if (rol == null || rol.isEmpty()) {
                    Predicate esCandidato = criteriaBuilder.like(
                        criteriaBuilder.lower(joinRoles.get("rol")), "%candidato%");
                    Predicate esEmpresa = criteriaBuilder.like(
                        criteriaBuilder.lower(joinRoles.get("rol")), "%empresa%");
                    Predicate esAdmin = criteriaBuilder.like(
                        criteriaBuilder.lower(joinRoles.get("rol")), "admin");

                    predicates.add(criteriaBuilder.or(esCandidato, esEmpresa, esAdmin));
                } else {
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(joinRoles.get("rol")), rol.toLowerCase()));
                }

            // Lógica para ADMIN
            } else if ("ADMIN".equalsIgnoreCase(rolUsuario)) {
                if (rol == null || rol.isEmpty()) {
                    Predicate esCandidato = criteriaBuilder.like(
                        criteriaBuilder.lower(joinRoles.get("rol")), "%candidato%");
                    Predicate esEmpresa = criteriaBuilder.like(
                        criteriaBuilder.lower(joinRoles.get("rol")), "%empresa%");
                    predicates.add(criteriaBuilder.or(esCandidato, esEmpresa));
                } else if (!"ADMIN".equalsIgnoreCase(rol)) {
                    System.out.println("este es el rol de la sesion actual y su id: " + rolUsuario + " " + rol);
                    predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(joinRoles.get("rol")), rol.toLowerCase()));
                }
            }

            // 🚨 Protección para evitar mostrar todo si no se aplicaron filtros
            if (predicates.isEmpty()) {
                return criteriaBuilder.disjunction(); // Equivale a "WHERE 1 = 0"
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}



        // public static Specification<Usuario> adminFiltros(String rolUsuario,String
        // nombre, String rol, Boolean estado) {
        // return (root, query, criteriaBuilder) -> {
        // List<Predicate> predicates = new ArrayList<>();

        // // Filtro por nombre
        // if (nombre != null && !nombre.isEmpty()) {
        // predicates.add(criteriaBuilder.like(
        // criteriaBuilder.lower(root.get("nombre")),
        // "%" + nombre.toLowerCase() + "%"));

        // }
        // if (rolUsuario.equals("SUPER_ADMIN")) {
        // // Si el rol del usuario es SUPER_ADMIN, no se aplica filtro por rol
        // // predicates.add(criteriaBuilder.equal(root.get("isActive"), estado));
        // } else {
        // // Si el rol del usuario no es SUPER_ADMIN, aplicar filtro por rol
        // predicates.add(criteriaBuilder.equal(root.get("isActive"), estado));
        // }
        // // Filtro por Actividad
        // if (estado != null) {
        // predicates.add(criteriaBuilder.equal(root.get("isActive"), estado));

        // }
        // Join<Usuario, Roles> joinRoles = root.join("roles", JoinType.INNER);

        // if (rol == null || rol.isEmpty()) {
        // // Por defecto: filtrar por CANDIDATO o EMPRESA si no se recibió rol
        // Predicate esCandidato = criteriaBuilder.like(
        // criteriaBuilder.lower(joinRoles.get("rol")), "%candidato%");
        // Predicate esEmpresa = criteriaBuilder.like(
        // criteriaBuilder.lower(joinRoles.get("rol")), "%empresa%");
        // Predicate esAdmin = criteriaBuilder.like(
        // criteriaBuilder.lower(joinRoles.get("rol")), "%admin%");

        // predicates.add(criteriaBuilder.or(esCandidato, esEmpresa, esAdmin));
        // } else {
        // // Si se recibió un rol específico, aplicar el filtro directo
        // predicates.add(criteriaBuilder.like(
        // criteriaBuilder.lower(joinRoles.get("rol")),
        // rol.toLowerCase() ));

        // }

        // return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        // };
        // }


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