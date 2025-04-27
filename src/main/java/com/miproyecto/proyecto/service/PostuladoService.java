package com.miproyecto.proyecto.service;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Postulado;
import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.model.CandidatoResumenDTO;
import com.miproyecto.proyecto.model.PostuladoDTO;
import com.miproyecto.proyecto.model.VacanteResumenDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.repos.PostuladoRepository;
import com.miproyecto.proyecto.repos.VacanteRepository;
import com.miproyecto.proyecto.util.NotFoundException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Map<String, Object> findByNvacantes(Long nvacantes, Pageable pageable) {
        final Vacante vacante = vacanteRepository.findById(nvacantes)
                .orElseThrow(NotFoundException::new);
        Page<PostuladoDTO> postulados = postuladoRepository.findByVacante(vacante, pageable)
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO())); 
        return mapResponse(postulados, "postulados");
    }


    public Map<String, Object> findByIdUsuario(Long  idUsuario, Pageable pageable) {
        final Candidato candidato = candidatoRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);

        Page<PostuladoDTO> postulados = postuladoRepository.findByCandidato(candidato, pageable)
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()));
        return mapResponse(postulados, "postulados");
    }

    public PostuladoDTO findByNvacantesAndIdUsuario(Long nvacanteId, Long idUsuarioId) {
        return postuladoRepository.findByVacante_NvacantesAndCandidato_IdUsuario(nvacanteId, idUsuarioId)
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))
                .orElse(null); 
    }
    

    public PostuladoDTO get(final Long nPostulacion) {
        return postuladoRepository.findById(nPostulacion)
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))
                .orElseThrow(NotFoundException::new);
    }
    
    public Long create(final PostuladoDTO postuladoDTO) {
        
        postuladoDTO.setVacante(null);
        postuladoDTO.setCandidato(null);
        postuladoDTO.setFechaPostulacion(LocalDate.now());
        postuladoDTO.setEstado("Espera");

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

    public void cambiarEstadoVacantes(Long Nvacante, boolean estado) {
        int postuladosAtualizados = postuladoRepository.actualizarEstadoPostulacionesPorVacante(Nvacante, estado);
        System.out.println("postulados actualizados: " + postuladosAtualizados);
    }

    public void cambiarEstadoPorUsuario(Long idUsuario, boolean estado) {
        int postuladosAtualizados = postuladoRepository.actualizarEstadoPostulacionesPorUsuario(idUsuario, estado);
        System.out.println("postulados actualizados: " + postuladosAtualizados);
    }

    public void delete(final Long nPostulacion) {
        postuladoRepository.deleteById(nPostulacion);
    }

    public Map<String,Object> mapResponse(Page<PostuladoDTO> pageableResponse, String nameList){
        Map<String,Object> response = new HashMap<>();

        response.put(nameList, pageableResponse.getContent());
        response.put("totalElements", pageableResponse.getTotalElements());
        response.put("pageActual", pageableResponse.getPageable());
        response.put("totalPage", pageableResponse.getTotalPages());

        return response;
    }
    
    private PostuladoDTO mapToDTO(final Postulado postulado, final PostuladoDTO postuladoDTO) {
        postuladoDTO.setnPostulacion(postulado.getNPostulacion());
        postuladoDTO.setFechaPostulacion(postulado.getFechaPostulacion());
        postuladoDTO.setEstado(postulado.getEstado());
        postuladoDTO.setVacante(
            vacanteService.mapToResumenDTO(postulado.getVacante(), new VacanteResumenDTO())
        );
        postuladoDTO.setCandidato(
            candidatoService.mapToResumenDTO(postulado.getCandidato(), new CandidatoResumenDTO ())
        );
        return postuladoDTO;
    }

    private Postulado mapToEntity(final PostuladoDTO postuladoDTO, final Postulado postulado) {
        postulado.setFechaPostulacion(postuladoDTO.getFechaPostulacion());
        postulado.setEstado(postuladoDTO.getEstado());
        final Vacante nvacante = vacanteRepository.findById(postuladoDTO.getVacante().getId())
                .orElseThrow(() -> new NotFoundException("nvacante not found"));
        postulado.setVacante(nvacante);
        final Candidato idUsuario = candidatoRepository.findById(postuladoDTO.getCandidato().getId())
                .orElseThrow(() -> new NotFoundException("idUsuario not found"));
        postulado.setCandidato(idUsuario);
        return postulado;
    }

}
