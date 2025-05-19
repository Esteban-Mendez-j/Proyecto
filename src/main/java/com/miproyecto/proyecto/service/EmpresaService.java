package com.miproyecto.proyecto.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.domain.Roles;
import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.model.EmpresaDTO;
import com.miproyecto.proyecto.repos.EmpresaRepository;
import com.miproyecto.proyecto.repos.RolesRepository;
import com.miproyecto.proyecto.repos.VacanteRepository;
import com.miproyecto.proyecto.util.NotFoundException;
import com.miproyecto.proyecto.util.ReferencedWarning;


@Service
@Transactional
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final VacanteRepository vacanteRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

  
    public EmpresaService(EmpresaRepository empresaRepository, VacanteRepository vacanteRepository,
            PasswordEncoder passwordEncoder, RolesRepository rolesRepository) {
        this.empresaRepository = empresaRepository;
        this.vacanteRepository = vacanteRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
    }

    public List<EmpresaDTO> findAll() {
        final List<Empresa> empresas = empresaRepository.findAll(Sort.by("idUsuario"));
        return empresas.stream()
                .map(empresa -> mapToDTO(empresa, new EmpresaDTO()))
                .toList();
    }

    // obtinene una empresa por el idUsuario
    public EmpresaDTO get(final Long idUsuario) {
        return empresaRepository.findById(idUsuario)
                .map(empresa -> mapToDTO(empresa, new EmpresaDTO()))
                .orElseThrow(NotFoundException::new);
    }
    
    public void create(final EmpresaDTO empresaDTO) {
        List<Roles> roles= new ArrayList<>();
        empresaDTO.setIsActive(true);
        empresaDTO.setFechaRegistro(LocalDate.now());
        roles.add(rolesRepository.findByRol("EMPRESA"));
        Empresa empresa = mapToEntity(empresaDTO, new Empresa(), true);
        empresa.setRoles(roles);
        empresaRepository.save(empresa);
    }

    public void update(final Long idUsuario, final EmpresaDTO empresaDTO) {
        final Empresa empresa = empresaRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        mapToEntity(empresaDTO, empresa, false);
        empresaRepository.save(empresa);
    }

    public void delete(final Long idUsuario) {
        empresaRepository.deleteById(idUsuario);
    }

    private EmpresaDTO mapToDTO(final Empresa empresa, final EmpresaDTO empresaDTO) {  
        empresaDTO.setIdUsuario(empresa.getIdUsuario());
        empresaDTO.setNombre(empresa.getNombre());
        empresaDTO.setContrasena(empresa.getContrasena());
        empresaDTO.setCorreo(empresa.getCorreo());
        empresaDTO.setTelefono(empresa.getTelefono());
        empresaDTO.setDescripcion(empresa.getDescripcion());
        empresaDTO.setImagen(empresa.getImagen());
        empresaDTO.setSectorEmpresarial(empresa.getSectorEmpresarial());
        empresaDTO.setSitioWeb(empresa.getSitioWeb());
        empresaDTO.setNit(empresa.getNit());
        empresaDTO.setIsActive(empresa.getIsActive());
        empresaDTO.setComentarioAdmin(empresa.getComentarioAdmin());
        empresaDTO.setRoles(
            empresa.getRoles().stream()
                    .map(roles -> roles.getRol())
                    .collect(Collectors.toList())
        );
        return empresaDTO;
    }

    private Empresa mapToEntity(final EmpresaDTO empresaDTO, final Empresa empresa, boolean create) {
        
        if (create) {
            empresa.setContrasena(passwordEncoder.encode(empresaDTO.getContrasena()));
            empresa.setIsActive(empresaDTO.getIsActive());
            empresa.setComentarioAdmin(empresaDTO.getComentarioAdmin());
        }
        empresa.setNombre(empresaDTO.getNombre());
        empresa.setCorreo(empresaDTO.getCorreo());
        empresa.setTelefono(empresaDTO.getTelefono());
        empresa.setDescripcion(empresaDTO.getDescripcion());
        empresa.setImagen(empresaDTO.getImagen());
        empresa.setSectorEmpresarial(empresaDTO.getSectorEmpresarial());
        empresa.setSitioWeb(empresaDTO.getSitioWeb());
        empresa.setNit(empresaDTO.getNit());
        // empresa.setRoles(
        //     empresaDTO.getRoles().stream()
        //             .map(roles -> rolesRepository.findByRol(roles))
        //             .collect(Collectors.toList())
        // );
        return empresa;
    }
    
    public boolean nitExists(final String nit) {
        return empresaRepository.existsByNitIgnoreCase(nit);
    }

    public boolean idUsuarioExists(final Long idUsuario) {
        return empresaRepository.existsByIdUsuario (idUsuario);
    }

    
    public ReferencedWarning getReferencedWarning(final Long idUsuario) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Empresa empresa = empresaRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        final Vacante idUsuarioVacante = vacanteRepository.findFirstByIdUsuario(empresa);
        if (idUsuarioVacante != null) {
            referencedWarning.setKey("empresa.vacante.idUsuario.referenced");
            referencedWarning.addParam(idUsuarioVacante.getNvacantes());
            return referencedWarning;
        }
        return null;
    }
}


    
//     public List<EmpresaDTO> buscarEmpresasConFiltro(EmpresaDTO filtro) {
//         Specification<Empresa> spec = UsuarioSpecifications.conFiltros(filtro);
//         List<Empresa> empresas = empresaRepository.findAll(spec);
//         return empresas.stream().map(this::convertirADTO).toList();
//     }

//     private EmpresaDTO convertirADTO(Empresa empresa) {
//         return null;
//         // conversión básica o usando MapStruct
//     }
// }

