package com.miproyecto.proyecto.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Postulado;
import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.model.CandidatoResumenDTO;
import com.miproyecto.proyecto.model.ChatDTO;
import com.miproyecto.proyecto.model.PostuladoDTO;
import com.miproyecto.proyecto.model.VacanteResumenDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.repos.PostuladoRepository;
import com.miproyecto.proyecto.repos.VacanteRepository;
import com.miproyecto.proyecto.util.NotFoundException;


@Service
@Transactional
public class PostuladoService {

    private final PostuladoRepository postuladoRepository;
    private final VacanteRepository vacanteRepository;
    private final CandidatoRepository candidatoRepository;
    private final CandidatoService candidatoService;
    private final VacanteService vacanteService;
    private final ChatService chatService;

    public PostuladoService(PostuladoRepository postuladoRepository, VacanteRepository vacanteRepository,
            CandidatoRepository candidatoRepository, CandidatoService candidatoService, VacanteService vacanteService,
            ChatService chatService) {
        this.postuladoRepository = postuladoRepository;
        this.vacanteRepository = vacanteRepository;
        this.candidatoRepository = candidatoRepository;
        this.candidatoService = candidatoService;
        this.vacanteService = vacanteService;
        this.chatService = chatService;
    }

    public List<PostuladoDTO> findAll() {
        final List<Postulado> postuladoes = postuladoRepository.findAll(Sort.by("nPostulacion"));
        return postuladoes.stream()
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))
                .toList();
    }

    public Map<String, Object> findByNvacantes(Long nvacantes, String estado, LocalDate fechaMinima, String nombreCandidato, Pageable pageable) {
        final Vacante vacante = vacanteRepository.findById(nvacantes)
                .orElseThrow(() -> new NotFoundException("Vacante no encontrada"));

        Page<PostuladoDTO> postulados = postuladoRepository.buscarPorFiltros(vacante, estado, fechaMinima, nombreCandidato, pageable)
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()));

        return mapResponse(postulados, "postulados");
    }


    public Map<String, Object> findByIdUsuario(Long idUsuario, String estado, String titulo, String empresa, LocalDate fechaMinima, Pageable pageable) {
        Candidato candidato = candidatoRepository.findById(idUsuario)
                .orElseThrow(NotFoundException::new);

        Page<PostuladoDTO> postulados = postuladoRepository
                .buscarPostulacionesFiltradas(candidato.getIdUsuario(), estado, titulo, empresa, fechaMinima, pageable)
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()));

        return mapResponse(postulados, "postulados");
    }



    public PostuladoDTO findByNvacantesAndIdUsuario(Long nvacanteId, Long idUsuarioId) {
        Vacante vacante = vacanteRepository.findById(nvacanteId).orElseThrow(NotFoundException::new);
        Candidato candidato =candidatoRepository.findById(idUsuarioId).orElseThrow(NotFoundException::new);
        
        return postuladoRepository.findByCandidatoAndVacante(candidato, vacante)
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))
                .orElse(null); 
    }
    

    public PostuladoDTO get(final Long nPostulacion) {
        return postuladoRepository.findById(nPostulacion)
                .map(postulado -> mapToDTO(postulado, new PostuladoDTO()))
                .orElseThrow(NotFoundException::new);
    }
    
    public Long create(final PostuladoDTO postuladoDTO, CandidatoResumenDTO candidatoResumenDTO, Long nVacante) {
        
        Vacante vacante = vacanteRepository.findById(nVacante).orElseThrow(NotFoundException::new);
        vacante.setTotalpostulaciones(vacante.getTotalpostulaciones()+1);
        vacanteRepository.save(vacante);

        postuladoDTO.setVacante(vacanteService.findVacanteResumenById(nVacante));
        postuladoDTO.setCandidato(candidatoResumenDTO);
        postuladoDTO.setFechaPostulacion(LocalDate.now());
        postuladoDTO.setActive(true);
        postuladoDTO.setVacanteIsActive(true);
        postuladoDTO.setEstado("Espera");

        final Postulado postulado = new Postulado();
        mapToEntity(postuladoDTO, postulado);
        return postuladoRepository.save(postulado).getNPostulacion();
    }
    
    public void cambiarEstado (PostuladoDTO postuladoDTO, Boolean estado){
        Vacante vacante = vacanteRepository.findById(postuladoDTO.getVacante().getId())
                .orElseThrow(NotFoundException::new);
        vacante.setTotalpostulaciones(vacante.getTotalpostulaciones()+1);
        vacanteRepository.save(vacante);

        postuladoDTO.setActive(estado);
        postuladoDTO.setFechaPostulacion(LocalDate.now());
        postuladoDTO.setEstado("Espera");
        Postulado postulado = mapToEntity(postuladoDTO, new Postulado());
        postulado.setNPostulacion(postuladoDTO.getnPostulacion());
        postuladoRepository.save(postulado);
    }

    public void update(Long nPostulacion, PostuladoDTO PostuladoDTO) {
        Postulado postulado = postuladoRepository.findById(nPostulacion)
                .orElseThrow(NotFoundException::new);
        postulado.setEstado(PostuladoDTO.getEstado());
        postuladoRepository.save(postulado);

        // Lógica del chat
        if ("Rechazada".equalsIgnoreCase(postulado.getEstado())) {
            ChatDTO chat = chatService.findByVacanteIdAndCandidatoId(
                    postulado.getVacante().getNvacantes(), 
                    postulado.getCandidato().getIdUsuario());
            if (chat != null) {
                chatService.cambiarEstadoChat(chat.getId(), false, "Lamentamos informarte que tu postulación ha sido rechazada. El chat se ha cerrado");
            }
        }
    }


    public void cambiarEstadoVacantes(Long Nvacante, boolean estado) {
        int numero = postuladoRepository.actualizarEstadoPostulacionesPorVacante(Nvacante, estado);
        System.out.println("numero "+numero);
    }

    public void cambiarEstadoPorUsuario(Long idUsuario, boolean estado) {
        postuladoRepository.actualizarEstadoPostulacionesPorUsuario(idUsuario, estado);
    }

    public void cancelarPostulacion (Long idUsuario, boolean estado, Long nVacante){
        Postulado postulado = postuladoRepository.findById(idUsuario).orElse(null);
        postulado.setActive(estado);
        postulado.setEstado("Cancelada");
        postuladoRepository.save(postulado);

        Vacante vacante = vacanteRepository.findById(nVacante).orElseThrow(NotFoundException::new);
        vacante.setTotalpostulaciones(vacante.getTotalpostulaciones()-1);
        vacanteRepository.save(vacante);

        ChatDTO chat = chatService.findByVacanteIdAndCandidatoId(
                    postulado.getVacante().getNvacantes(), 
                    postulado.getCandidato().getIdUsuario());
        if (chat != null) {
            chatService.cambiarEstadoChat(chat.getId(), false, "El candidato cancelo la postulacion");
        }
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
        postuladoDTO.setActive(postulado.isActive());
        postuladoDTO.setVacanteIsActive(postulado.isVacanteIsActive());
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
        postulado.setActive(postuladoDTO.isActive());
        postulado.setVacanteIsActive(postuladoDTO.isVacanteIsActive());       
        return postulado;
    }

}
