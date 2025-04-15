package com.miproyecto.proyecto.service;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Postulado;
import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.model.CandidatoDTO;
import com.miproyecto.proyecto.model.PostuladoDTO;
import com.miproyecto.proyecto.model.VacanteDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.repos.PostuladoRepository;
import com.miproyecto.proyecto.repos.VacanteRepository;
import com.miproyecto.proyecto.util.NotFoundException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class PostuladoService {

    private final PostuladoRepository postuladoRepository;
    private final VacanteRepository vacanteRepository;
    private final CandidatoRepository candidatoRepository;
    private final CandidatoService candidatoService;
    private final VacanteService vacanteService;

    public PostuladoService(final PostuladoRepository postuladoRepository,
            final VacanteRepository vacanteRepository,
            final CandidatoRepository candidatoRepository,
            final CandidatoService candidatoService,
            final VacanteService vacanteService) {
        this.postuladoRepository = postuladoRepository;
        this.vacanteRepository = vacanteRepository;
        this.candidatoRepository = candidatoRepository;
        this.candidatoService = candidatoService;
        this.vacanteService = vacanteService;
    }

    public List<PostuladoDTO> findAll() {
        final List<Postulado> postuladoes = postuladoRepository.findAll(Sort.by("nPostulacion"));
        return postuladoes.stream()
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))
                .toList();
    }

    // obtienen todos los psotulados registrados en la misma vacante 
    public List<PostuladoDTO> findByNvacantes(Long nvacantes) {
        final Vacante vacante = vacanteRepository.findById(nvacantes)
                .orElseThrow(NotFoundException::new);

        final List<Postulado> postuladoes = postuladoRepository.findByNvacante(vacante);
        return postuladoes.stream()
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))
                .toList(); 
    }


    public List<PostuladoDTO> findByIdUsuario(Long  idUsuario) {
        final Candidato candidato = candidatoRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);

        final List<Postulado> postuladoes = postuladoRepository.findByIdUsuario(candidato);
        return postuladoes.stream()
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))
                .toList(); 
    }

    public PostuladoDTO findByNvacantesAndIdUsuario(Long nvacanteId, Long idUsuarioId) {
        return postuladoRepository.findByNvacante_NvacantesAndIdUsuario_IdUsuario(nvacanteId, idUsuarioId)
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))
                .orElse(null); 
    }
    

    public PostuladoDTO get(final Long nPostulacion) {
        return postuladoRepository.findById(nPostulacion)
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    // para hallar el candidato de cada postulacion
    public Map<Long, CandidatoDTO> obtenerCandidatosUnicosPorVacante(Long nvacantes) {
        // Obtener la vacante por su ID
        Vacante vacante = vacanteRepository.findById(nvacantes).orElseThrow(NotFoundException::new);
    
        // Obtener todas las postulaciones de la vacante específica
        List<Postulado> postulados = postuladoRepository.findByNvacante(vacante);
    
        // Convertir la lista de Postulado a PostuladoDTO
        List<PostuladoDTO> postuladosDTO = postulados.stream()
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))  // Asegúrate de que mapToDTO sea correcto
                .collect(Collectors.toList());  // Usamos collect para convertir a lista
    
        // Mapeo de candidatos únicos por su ID
        Map<Long, CandidatoDTO> candidatosMap = new HashMap<>();
    
        for (PostuladoDTO postuladoDTO : postuladosDTO) {
            Long idUsuario = postuladoDTO.getIdUsuario();
            // Solo agrega al candidato si no existe ya en el mapa
            candidatosMap.computeIfAbsent(idUsuario, id -> {
                // Obtener el candidato desde el repositorio
                Optional<Candidato> candidatoOptional = candidatoRepository.findById(id);
                
                // Verificar si el candidato está presente
                if (candidatoOptional.isPresent()) {
                    // Si está presente, mapeamos el Candidato a CandidatoDTO
                    return candidatoService.mapToDTO(candidatoOptional.get(), new CandidatoDTO());
                } else {
                    // Si no lo encontramos, agregamos null
                    return null;
                }
            });
        }
    
        return candidatosMap;
    }
    
    
    
    public Map<Long, VacanteDTO> findVacantesByIdUsuario(Long idUsuario) {
        // Mapa para almacenar vacantes mapeadas por su ID
        Map<Long, VacanteDTO> vacantesDTOMap = new HashMap<>();
        
        // Obtener las postulaciones del usuario
        List<PostuladoDTO> postulados = findByIdUsuario(idUsuario);
        
        // Crear un Set para almacenar los IDs de las vacantes ya procesadas
        Set<Long> vacantesIds = new HashSet<>();
        
        // Recorrer las postulaciones para obtener las vacantes
        for (PostuladoDTO postulado : postulados) {
            Long nvacante = postulado.getNvacante();
            
            // Evitar procesar la misma vacante más de una vez
            if (vacantesIds.add(nvacante)) {
                // Obtener la vacante por su ID
                Vacante vacante = vacanteRepository.findById(nvacante).orElse(null);
                
                if (vacante != null) {
                    // Mapear la vacante a VacanteDTO y agregarla al mapa
                    VacanteDTO vacanteDTO = vacanteService.mapToDTO(vacante, new VacanteDTO());
                    vacantesDTOMap.put(nvacante, vacanteDTO);
                }
            }
        }
        
        return vacantesDTOMap;
    }
    

    public Long create(final PostuladoDTO postuladoDTO) {
        final Postulado postulado = new Postulado();
        mapToEntity(postuladoDTO, postulado);
        return postuladoRepository.save(postulado).getNPostulacion();
    }

    public void update(final Long nPostulacion, final PostuladoDTO postuladoDTO) {
        final Postulado postulado = postuladoRepository.findById(nPostulacion)
                .orElseThrow(NotFoundException::new);
        mapToEntity(postuladoDTO, postulado);
        postuladoRepository.save(postulado);
    }

    public void delete(final Long nPostulacion) {
        postuladoRepository.deleteById(nPostulacion);
    }

    private PostuladoDTO mapToDTO(final Postulado postulado, final PostuladoDTO postuladoDTO) {
        postuladoDTO.setNPostulacion(postulado.getNPostulacion());
        postuladoDTO.setFechaPostulacion(postulado.getFechaPostulacion());
        postuladoDTO.setEstado(postulado.getEstado());
        postuladoDTO.setNvacante(postulado.getNvacante() == null ? null : postulado.getNvacante().getNvacantes());
        postuladoDTO.setIdUsuario(postulado.getIdUsuario() == null ? null : postulado.getIdUsuario().getIdUsuario());
        return postuladoDTO;
    }

    private Postulado mapToEntity(final PostuladoDTO postuladoDTO, final Postulado postulado) {
        postulado.setFechaPostulacion(postuladoDTO.getFechaPostulacion());
        postulado.setEstado(postuladoDTO.getEstado());
        final Vacante nvacante = postuladoDTO.getNvacante() == null ? null : vacanteRepository.findById(postuladoDTO.getNvacante())
                .orElseThrow(() -> new NotFoundException("nvacante not found"));
        postulado.setNvacante(nvacante);
        final Candidato idUsuario = postuladoDTO.getIdUsuario() == null ? null : candidatoRepository.findById(postuladoDTO.getIdUsuario())
                .orElseThrow(() -> new NotFoundException("idUsuario not found"));
        postulado.setIdUsuario(idUsuario);
        return postulado;
    }

}
