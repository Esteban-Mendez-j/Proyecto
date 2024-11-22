package com.miproyecto.proyecto.controller;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.domain.Vacante;
import com.miproyecto.proyecto.model.CandidatoDTO;
import com.miproyecto.proyecto.model.PostuladoDTO;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.model.VacanteDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.repos.VacanteRepository;
import com.miproyecto.proyecto.service.CandidatoService;
import com.miproyecto.proyecto.service.EncryptionService;
import com.miproyecto.proyecto.service.PostuladoService;
import com.miproyecto.proyecto.util.CustomCollectors;
import com.miproyecto.proyecto.util.WebUtils;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/postulados")
public class PostuladoController {

    private final PostuladoService postuladoService;
    private final VacanteRepository vacanteRepository;
    private final CandidatoRepository candidatoRepository;
    private final EncryptionService encryptionService;
    private final CandidatoService candidatoService;

    public PostuladoController(final PostuladoService postuladoService,
            final VacanteRepository vacanteRepository,
            final CandidatoRepository candidatoRepository,
            final CandidatoService candidatoService) {
        this.postuladoService = postuladoService;
        this.vacanteRepository = vacanteRepository;
        this.candidatoRepository = candidatoRepository;
        this.candidatoService = candidatoService;
        this.encryptionService = new EncryptionService();
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("nvacanteValues", vacanteRepository.findAll(Sort.by("nvacantes"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Vacante::getNvacantes, Vacante::getCargo)));
        model.addAttribute("idUsuarioValues", candidatoRepository.findAll(Sort.by("idUsuario"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Candidato::getIdUsuario, Candidato::getIdUsuario)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("postuladoes", postuladoService.findAll());
        return "postulado/list";
    }

    

    @GetMapping("/lista/{nvacantes}")
    public String listaByNvacantes(@PathVariable(name = "nvacantes") final String nvacantesEncrypt, final Model model) {
        
        Long nvacantes = encryptionService.decrypt(nvacantesEncrypt);
        
        // Obtener las postulaciones
        model.addAttribute("postulados", postuladoService.findByNvacantes(nvacantes));  // Se añaden los postulados al modelo

        // Obtener los candidatos únicos asociados a estas postulaciones
        Map<Long, CandidatoDTO> candidatosMap = postuladoService.obtenerCandidatosUnicosPorVacante(nvacantes);
        model.addAttribute("candidatos", candidatosMap);  // Añadir el mapa completo de candidatos al modelo

        return "postulado/list";
    }


    @GetMapping("/lista/candidato")
    public String listaByIdUsuario(final Model model, HttpSession session) {
        
        // Sacamos el ID del usuario que inicia sesion
        final UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");

        Long IdUsuario = usuario.getIdUsuario();

        // Obtener el mapa de vacantes por 'nvacante'
        Map<Long, VacanteDTO> vacanteMap = postuladoService.findVacantesByIdUsuario(IdUsuario);

        // Añadir los postulados y el mapa de vacantes al modelo
        model.addAttribute("postulados", postuladoService.findByIdUsuario(IdUsuario));
        model.addAttribute("vacante", vacanteMap);  // El mapa de vacantes
        return "postulado/list";
    }


    @PostMapping("/add/{nvacantes}")
    public String add(@PathVariable(name = "nvacantes") final String nvacantesEncrypt, 
        final RedirectAttributes redirectAttributes, 
        HttpSession session) {

        // Sacamos el ID del usuario que inicia sesion
        final UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");

        Long idUsuarioId = usuario.getIdUsuario();
        Long nvacantes = encryptionService.decrypt(nvacantesEncrypt);
       
        if(postuladoService.findByNvacantesAndIdUsuario( nvacantes, idUsuarioId) != null){
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Ya postulaste a esta vacante");
            return "redirect:/vacantes/seleccion/{nvacantes}";
        }

        CandidatoDTO candidatoDTO = candidatoService.get(idUsuarioId);
        boolean estudio = candidatoService.estudiosExist(idUsuarioId);


        if(candidatoDTO.getDescripcion() == null || estudio == false || candidatoDTO.getTelefono() == null){
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, "Completa tu perfil con descripción, teléfono y al menos un estudio antes de postularte.");
            return "redirect:/vacantes/seleccion/{nvacantes}";
        }

        // Crear la postulación con el id de vacante y otros valores por defecto
        PostuladoDTO postuladoDTO = new PostuladoDTO();
        postuladoDTO.setIdUsuario(idUsuarioId);
        postuladoDTO.setNvacante(nvacantes);
        postuladoDTO.setFechaPostulacion(LocalDate.now());
        postuladoDTO.setEstado("Espera");

        postuladoService.create(postuladoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("postulado.create.success"));
        return "redirect:/vacantes/seleccion/{nvacantes}";
    }


    @PostMapping("/edit/{nPostulacion}")
    public String edit(@PathVariable(name = "nPostulacion") final String nPostulacionEncrypt, 
        @RequestParam(name = "nuevoEstado") String nuevoEstado, Model model,
        final RedirectAttributes redirectAttributes) {
        
        Long nPostulacion = encryptionService.decrypt(nPostulacionEncrypt);
        
        PostuladoDTO postuladoDTO = postuladoService.get(nPostulacion);
        postuladoDTO.setEstado(nuevoEstado);
        postuladoService.update(nPostulacion, postuladoDTO);

        redirectAttributes.addAttribute("nvacantes", postuladoDTO.getNvacanteEncrypt());

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("postulado.update.success"));
        return "redirect:/postulados/lista/{nvacantes}";
    }

    @PostMapping("/delete/{nPostulacion}")
    public String delete(@PathVariable(name = "nPostulacion") final String nPostulacionEncrypt,
            final RedirectAttributes redirectAttributes) {
        Long nPostulacion = encryptionService.decrypt(nPostulacionEncrypt);        
        postuladoService.delete(nPostulacion);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("postulado.delete.success"));
        return "redirect:/postulados/lista/candidato";
    }

}
