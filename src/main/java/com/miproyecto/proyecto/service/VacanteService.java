package com.miproyecto.proyecto.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.domain.Postulado;
import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.model.FiltroVacanteDTO;
import com.miproyecto.proyecto.model.VacanteDTO;
import com.miproyecto.proyecto.model.VacanteResumenDTO;
import com.miproyecto.proyecto.repos.EmpresaRepository;
import com.miproyecto.proyecto.repos.PostuladoRepository;
import com.miproyecto.proyecto.repos.VacanteRepository;
import com.miproyecto.proyecto.repos.VacanteSpecifications;
import com.miproyecto.proyecto.util.NotFoundException;
import com.miproyecto.proyecto.util.ReferencedWarning;


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

    // listado de todas las vacantes activas
    public Map<String,Object> findAllByEstado(boolean estado, Pageable pageable, String nameList) {
        final Page<Vacante> vacantes = vacanteRepository.findByIsActiveOrderByFechaPublicacionDesc(estado, pageable);
        final Page<VacanteDTO> vacantesDTO = vacantes.map(vacante -> mapToDTO(0L,vacante, new VacanteDTO()));
        return mapResponse(vacantesDTO, nameList) ;       
    }

    public Map<String,Object> mapResponse(Page<VacanteDTO> pageableResponse, String nameList){
        Map<String,Object> response = new HashMap<>();

        response.put(nameList, pageableResponse.getContent());
        response.put("totalElements", pageableResponse.getTotalElements());
        response.put("pageActual", pageableResponse.getPageable());
        response.put("totalPage", pageableResponse.getTotalPages());
        return response;
    }

    // listado de las vacantes que esten relacionados con el idUsuario
    public Map<String,Object> findByIdUsuario(Long idUsuario, Pageable pageable) {
        // Obtener la empresa usando su id
        Empresa empresa = empresaRepository.findById(idUsuario)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada"));
        
        // Obtener las vacantes relacionadas con esa empresa
        Page<Vacante> vacantes = vacanteRepository.findByIdUsuario(empresa, pageable);
        
        // Convertir cada vacante a VacanteDTO y devolver la lista
        Page<VacanteDTO> vacantesDTO = vacantes.map(vacante -> mapToDTO(0L,vacante, new VacanteDTO()));
        return mapResponse(vacantesDTO, "vacantes");
                
    }

    public VacanteDTO findByIdUsuarioAndNvacante(Long idUsuario, Long nVacante){
        Empresa empresa = empresaRepository.findById(idUsuario)
                .orElse(null);

        return vacanteRepository.findByIdUsuarioAndNvacantes(empresa, nVacante)
            .map(vacante -> mapToDTO(0L,vacante, new VacanteDTO()))
            .orElse(null);
    }
    
    public Map<String, Object> buscarVacantesConFiltros( Long idLogin, FiltroVacanteDTO filtro, Pageable pageable) {
        Specification<Vacante> specification = VacanteSpecifications.conFiltros(filtro);
        Page<VacanteDTO> page = vacanteRepository.findAll(specification, pageable).map(vacante -> mapToDTO(idLogin,vacante, new VacanteDTO()));
        
        return mapResponse(page, "vacantes");         
    }

    public List<VacanteDTO> TopVacantesPorPostulados(Long idEmpresa){
        
        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElse(null);

        return vacanteRepository.findTop6ByIdUsuarioOrderByTotalpostulacionesDesc(empresa).stream()
            .map(v -> mapToDTO(0L, v, new VacanteDTO()))
            .collect(Collectors.toList());  
        
    }

    public List<VacanteDTO> TopVacantesPorFechaSueldoExperiencia(Long idLogin) {
        Set<Vacante> topVacantes = new HashSet<>();
        
        topVacantes.addAll(vacanteRepository.findTop2ByIsActiveOrderByFechaPublicacionDesc(true));
        topVacantes.addAll(vacanteRepository.findTop2ByIsActiveOrderByExperienciaAsc(true));
        topVacantes.addAll(vacanteRepository.findTop2ByIsActiveOrderBySueldoDesc(true));

        // Convertir a DTO
        return topVacantes.stream()
            .filter(vacante -> vacante != null)
            .map(vacante -> mapToDTO(idLogin ,vacante, new VacanteDTO()))
            .collect(Collectors.toList());
    }

    public VacanteDTO get(Long idLogin, final Long nvacantes) {
        return vacanteRepository.findById(nvacantes)
                .map(vacante -> mapToDTO(idLogin,vacante, new VacanteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public VacanteResumenDTO findVacanteResumenById(final Long nvacantes) {
        return vacanteRepository.findById(nvacantes)
                .map(vacante -> mapToResumenDTO(vacante, new VacanteResumenDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public void create(final VacanteDTO vacanteDTO) {
        final Vacante vacante = new Vacante();
        vacanteDTO.setFechaPublicacion(LocalDate.now());
        mapToEntity(vacanteDTO, vacante);
        vacante.setIsActive(true);
        vacante.setActivaPorEmpresa(true);
        vacanteRepository.save(vacante);
    }

    public void update(final Long nvacantes, final VacanteDTO vacanteDTO) {
        final Vacante vacante = vacanteRepository.findById(nvacantes)
                .orElseThrow(NotFoundException::new);
        vacanteDTO.setIdUsuario(vacante.getIdUsuario().getIdUsuario());
        vacanteDTO.setFechaPublicacion(vacante.getFechaPublicacion());
        vacanteDTO.setActive(vacante.getIsActive());
        vacanteDTO.setActivaPorEmpresa(vacante.isActivaPorEmpresa());
        mapToEntity(vacanteDTO, vacante);
        vacanteRepository.save(vacante);
    }

    public void cambiarEstado(final Long nvacantes, boolean estado) {
        final Vacante vacante = vacanteRepository.findById(nvacantes)
                .orElseThrow(NotFoundException::new);
        vacante.setActivaPorEmpresa(estado);
        vacanteRepository.save(vacante);
    }
    
    public VacanteDTO mapToDTO(Long idPostulaciones, final Vacante vacante, final VacanteDTO vacanteDTO) {
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
        vacanteDTO.setActive(vacante.getIsActive());
        vacanteDTO.setComentarioAdmin(vacante.getComentarioAdmin());
        vacanteDTO.setIdUsuario(vacante.getIdUsuario() == null ? null : vacante.getIdUsuario().getIdUsuario());
        vacanteDTO.setNameEmpresa(vacante.getIdUsuario() != null ? vacante.getIdUsuario().getNombre() : "Empresa Desconocida");
        vacanteDTO.setImagenEmpresa(vacante.getIdUsuario() != null ? vacante.getIdUsuario().getImagen() : "null");
        vacanteDTO.setnPostulados(vacante.getLitarpostulados().size());
        vacanteDTO.setTotalpostulaciones(vacante.getTotalpostulaciones());
        vacanteDTO.setActivaPorEmpresa(vacante.isActivaPorEmpresa());
        vacanteDTO.setCandidatoPostulado(
            vacante.getLitarpostulados()
                .stream()
                .anyMatch(p -> p.getCandidato().getIdUsuario().equals(idPostulaciones))
        );
        vacante.getLitarpostulados().stream()
            .filter(p -> p.getCandidato().getIdUsuario().equals(idPostulaciones))
            .findFirst()
            .ifPresent(p -> vacanteDTO.setEstadoPostulacion(p.getEstado())); // o p.getEstado().name()
        return vacanteDTO;
    }

    public VacanteResumenDTO mapToResumenDTO(final Vacante vacante, final VacanteResumenDTO vacanteResumenDTO){
        vacanteResumenDTO.setId(vacante.getNvacantes());
        vacanteResumenDTO.setCiudad(vacante.getCiudad());
        vacanteResumenDTO.setTitulo(vacante.getTitulo());
        vacanteResumenDTO.setTipo(vacante.getTipo());
        vacanteResumenDTO.setActivaPorEmpresa(vacante.isActivaPorEmpresa());
        vacanteResumenDTO.setIsActive(vacante.getIsActive());
        return vacanteResumenDTO;
    }

    public Vacante mapToEntity(final VacanteDTO vacanteDTO, final Vacante vacante) {
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
        vacante.setIsActive(vacanteDTO.isActive());
        vacante.setActivaPorEmpresa(vacanteDTO.isActivaPorEmpresa());
        vacante.setComentarioAdmin(vacanteDTO.getComentarioAdmin());
        final Empresa idUsuario = vacanteDTO.getIdUsuario() == null ? null : empresaRepository.findById(vacanteDTO.getIdUsuario())
                .orElseThrow(() -> new NotFoundException("idUsuario not found"));
        vacante.setIdUsuario(idUsuario);
        return vacante;
    }

    public ReferencedWarning getReferencedWarning(final Long nvacantes) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Vacante vacante = vacanteRepository.findById(nvacantes)
                .orElseThrow(NotFoundException::new);
        final Postulado nvacantePostulado = postuladoRepository.findFirstByVacante(vacante);
        if (nvacantePostulado != null) {
            referencedWarning.setKey("vacante.postulado.nvacante.referenced");
            referencedWarning.addParam(nvacantePostulado.getNPostulacion());
            return referencedWarning;
        }
        return null;
    }

}
