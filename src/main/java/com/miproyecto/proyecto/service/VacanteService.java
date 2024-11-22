package com.miproyecto.proyecto.service;

import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.domain.Postulado;
import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.model.VacanteDTO;
import com.miproyecto.proyecto.repos.EmpresaRepository;
import com.miproyecto.proyecto.repos.PostuladoRepository;
import com.miproyecto.proyecto.repos.VacanteRepository;
import com.miproyecto.proyecto.repos.VacanteSpecifications;
import com.miproyecto.proyecto.util.NotFoundException;
import com.miproyecto.proyecto.util.ReferencedWarning;
import java.util.List;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class VacanteService {

    private final VacanteRepository vacanteRepository;
    private final EmpresaRepository empresaRepository;
    private final PostuladoRepository postuladoRepository;

    public VacanteService(final VacanteRepository vacanteRepository,
            final EmpresaRepository empresaRepository,
            final PostuladoRepository postuladoRepository) {
        this.vacanteRepository = vacanteRepository;
        this.empresaRepository = empresaRepository;
        this.postuladoRepository = postuladoRepository;
    }

    // listado de todas las vacantes 
    public List<VacanteDTO> findAll() {
        final List<Vacante> vacantes = vacanteRepository.findAll(Sort.by("nvacantes"));
        return vacantes.stream()
                .map(vacante -> mapToDTO(vacante, new VacanteDTO()))
                .toList();
    }

    // listado de las vacantes que esten relacionados con el idUsuario
    public List<VacanteDTO> findByIdUsuario(Long idUsuario) {
        // Obtener la empresa usando su id
        Empresa empresa = empresaRepository.findById(idUsuario)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada"));
        
        // Obtener las vacantes relacionadas con esa empresa
        List<Vacante> vacantes = vacanteRepository.findByIdUsuario(empresa);
        
        // Convertir cada vacante a VacanteDTO y devolver la lista
        return vacantes.stream()
                .map(vacante -> mapToDTO(vacante, new VacanteDTO()))
                .toList();
    }
    

    public List<VacanteDTO> buscarVacantesConFiltros(VacanteDTO filtro) {
        // Llamas a la especificación y obtienes los resultados filtrados
        Specification<Vacante> specification = VacanteSpecifications.conFiltros(filtro);
        return vacanteRepository.findAll(specification).stream()
            .map(vacante -> mapToDTO(vacante, new VacanteDTO()))
            .toList(); // Obtienes las vacantes filtradas
    }


    public VacanteDTO get(final Long nvacantes) {
        return vacanteRepository.findById(nvacantes)
                .map(vacante -> mapToDTO(vacante, new VacanteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final VacanteDTO vacanteDTO) {
        final Vacante vacante = new Vacante();
        mapToEntity(vacanteDTO, vacante);
        return vacanteRepository.save(vacante).getNvacantes();
    }

    public void update(final Long nvacantes, final VacanteDTO vacanteDTO) {
        final Vacante vacante = vacanteRepository.findById(nvacantes)
                .orElseThrow(NotFoundException::new);
        mapToEntity(vacanteDTO, vacante);
        vacanteRepository.save(vacante);
    }

    public void delete(final Long nvacantes) {
        vacanteRepository.deleteById(nvacantes);
    }

    
    public VacanteDTO mapToDTO(final Vacante vacante, final VacanteDTO vacanteDTO) {
        vacanteDTO.setNvacantes(vacante.getNvacantes());
        vacanteDTO.setCargo(vacante.getCargo());
        vacanteDTO.setFechaPublicacion(vacante.getFechaPublicacion());
        vacanteDTO.setSueldo(vacante.getSueldo());
        vacanteDTO.setModalidad(vacante.getModalidad());
        vacanteDTO.setExperiencia(vacante.getExperiencia());
        vacanteDTO.setCiudad(vacante.getCiudad());
        vacanteDTO.setDepartamento(vacante.getDepartamento());
        vacanteDTO.setTitulo(vacante.getTitulo());
        vacanteDTO.setTipo(vacante.getTipo());
        vacanteDTO.setDescripcion(vacante.getDescripcion());
        vacanteDTO.setRequerimientos(vacante.getRequerimientos());
        vacanteDTO.setIdUsuario(vacante.getIdUsuario() == null ? null : vacante.getIdUsuario().getIdUsuario());
        vacanteDTO.setNameEmpresa(vacante.getIdUsuario() != null ? vacante.getIdUsuario().getNombre() : "Empresa Desconocida");
        vacanteDTO.setImagenEmpresa(vacante.getIdUsuario() != null ? vacante.getIdUsuario().getImagen() : "null");
        return vacanteDTO;
    }


    private Vacante mapToEntity(final VacanteDTO vacanteDTO, final Vacante vacante) {
        vacante.setCargo(vacanteDTO.getCargo());
        vacante.setFechaPublicacion(vacanteDTO.getFechaPublicacion());
        vacante.setSueldo(vacanteDTO.getSueldo());
        vacante.setModalidad(vacanteDTO.getModalidad());
        vacante.setExperiencia(vacanteDTO.getExperiencia());
        vacante.setCiudad(vacanteDTO.getCiudad());
        vacante.setDepartamento(vacanteDTO.getDepartamento());
        vacante.setTitulo(vacanteDTO.getTitulo());
        vacante.setTipo(vacanteDTO.getTipo());
        vacante.setDescripcion(vacanteDTO.getDescripcion());
        vacante.setRequerimientos(vacanteDTO.getRequerimientos());
        final Empresa idUsuario = vacanteDTO.getIdUsuario() == null ? null : empresaRepository.findById(vacanteDTO.getIdUsuario())
                .orElseThrow(() -> new NotFoundException("idUsuario not found"));
        vacante.setIdUsuario(idUsuario);
        return vacante;
    }

    public boolean idempresaExists(final Long idUsuario) {
        return vacanteRepository.existsById(idUsuario);
    }

    public ReferencedWarning getReferencedWarning(final Long nvacantes) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Vacante vacante = vacanteRepository.findById(nvacantes)
                .orElseThrow(NotFoundException::new);
        final Postulado nvacantePostulado = postuladoRepository.findFirstByNvacante(vacante);
        if (nvacantePostulado != null) {
            referencedWarning.setKey("vacante.postulado.nvacante.referenced");
            referencedWarning.addParam(nvacantePostulado.getNPostulacion());
            return referencedWarning;
        }
        return null;
    }

}
