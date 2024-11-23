package com.miproyecto.proyecto.service;

import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.model.EmpresaDTO;
import com.miproyecto.proyecto.repos.EmpresaRepository;
import com.miproyecto.proyecto.repos.UsuarioRepository;
import com.miproyecto.proyecto.repos.VacanteRepository;
import com.miproyecto.proyecto.util.NotFoundException;
import com.miproyecto.proyecto.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final VacanteRepository vacanteRepository;
    

    public EmpresaService(final EmpresaRepository empresaRepository, final VacanteRepository vacanteRepository, final UsuarioRepository usuarioRepository) {
        this.empresaRepository = empresaRepository;
        this.vacanteRepository = vacanteRepository; 
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
        final Empresa empresa = new Empresa();
        mapToEntity(empresaDTO, empresa);
        empresaRepository.save(empresa).getIdUsuario();
    }

    public void update(final Long idUsuario, final EmpresaDTO empresaDTO) {
        final Empresa empresa = empresaRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);
        mapToEntity(empresaDTO, empresa);
        empresaRepository.save(empresa);
    }

    public void delete(final Long idUsuario) {
        empresaRepository.deleteById(idUsuario);
    }

    private EmpresaDTO mapToDTO(final Empresa empresa, final EmpresaDTO empresaDTO) {  
        empresaDTO.setIdUsuario(empresa.getIdUsuario());
        empresaDTO.setTipo(empresa.getTipo());
        empresaDTO.setNombre(empresa.getNombre());
        empresaDTO.setContrasena(empresa.getContrasena());
        empresaDTO.setCorreo(empresa.getCorreo());
        empresaDTO.setTelefono(empresa.getTelefono());
        empresaDTO.setDescripcion(empresa.getDescripcion());
        empresaDTO.setImagen(empresa.getImagen());
        empresaDTO.setSectorEmpresarial(empresa.getSectorEmpresarial());
        empresaDTO.setSitioWeb(empresa.getSitioWeb());
        empresaDTO.setNit(empresa.getNit());
        return empresaDTO;
    }

    private Empresa mapToEntity(final EmpresaDTO empresaDTO, final Empresa empresa) {
        empresa.setTipo(empresaDTO.getTipo());
        empresa.setNombre(empresaDTO.getNombre());
        empresa.setContrasena(empresaDTO.getContrasena());
        empresa.setCorreo(empresaDTO.getCorreo());
        empresa.setTelefono(empresaDTO.getTelefono());
        empresa.setDescripcion(empresaDTO.getDescripcion());
        empresa.setImagen(empresaDTO.getImagen());
        empresa.setSectorEmpresarial(empresaDTO.getSectorEmpresarial());
        empresa.setSitioWeb(empresaDTO.getSitioWeb());
        empresa.setNit(empresaDTO.getNit());

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
