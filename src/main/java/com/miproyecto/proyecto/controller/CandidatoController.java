package com.miproyecto.proyecto.controller;

import com.miproyecto.proyecto.domain.Usuario;
import com.miproyecto.proyecto.model.CandidatoDTO;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.repos.UsuarioRepository;
import com.miproyecto.proyecto.service.CandidatoService;
import com.miproyecto.proyecto.service.EncryptionService;
import com.miproyecto.proyecto.service.EstudioService;
import com.miproyecto.proyecto.service.HistorialLaboralService;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.util.CustomCollectors;
import com.miproyecto.proyecto.util.ReferencedWarning;
import com.miproyecto.proyecto.util.WebUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.io.IOException;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@RequestMapping("/candidatos")
public class CandidatoController {

    private final CandidatoService candidatoService;
    private final UsuarioRepository usuarioRepository;
    private final EncryptionService encryptionService;
    private final HistorialLaboralService historialLaboralService;
    private final EstudioService estudioService;
    private final UsuarioService usuarioService;

    public CandidatoController(final CandidatoService candidatoService,
            final UsuarioRepository usuarioRepository, 
            final HistorialLaboralService historialLaboralService,
            final EstudioService estudioService,
            final UsuarioService usuarioService) {
        this.candidatoService = candidatoService;
        this.usuarioRepository = usuarioRepository;
        this.encryptionService = new EncryptionService();
        this.historialLaboralService = historialLaboralService;
        this.estudioService =  estudioService; 
        this.usuarioService = usuarioService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("idUsuarioValues", usuarioRepository.findAll(Sort.by("idUsuario"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Usuario::getIdUsuario, Usuario::getTipo)));
        model.addAttribute("tipo", "candidato");
    }

        

    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session, 
            @RequestParam(value = "idUsuario", required = false) String idUsuarioEncrypt,
            Model model) {
        final UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");
        
        Long idUsuario ;
    
        if (idUsuarioEncrypt == null) {
            idUsuario = usuario.getIdUsuario();
        } else{
            idUsuario = encryptionService.decrypt(idUsuarioEncrypt);
        }
        
        CandidatoDTO candidatoDTO = candidatoService.get(idUsuario);
        if (candidatoDTO != null) {
            model.addAttribute("estudios", estudioService.getEstudiosByIdUsuario(idUsuario));
            model.addAttribute("historialLaboral", historialLaboralService.getHistorialByIdUsuario(idUsuario));
            model.addAttribute("candidato", candidatoDTO);

            return "candidato/perfil"; 
        } else {
            return "redirect:/usuarios/login";
        }
    }


    @GetMapping("/add")
    public String add(@ModelAttribute("candidato") final CandidatoDTO candidatoDTO) {
        return "html/registro";
    }


    @PostMapping("/add")
    public String add(@ModelAttribute("candidato") @Valid final CandidatoDTO candidatoDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            return "html/registro";
        }
        candidatoService.create(candidatoDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("candidato.create.success"));
        return "redirect:/usuarios/login";
    }

    @GetMapping("/edit/{idUsuario}")
    public String edit(@PathVariable(name = "idUsuario") final String idUsuarioEncrypt,
            final Model model) {
        Long idUsuario = encryptionService.decrypt(idUsuarioEncrypt);
        model.addAttribute("candidato", candidatoService.get(idUsuario));
        return "candidato/edit";
    }

    
    @PostMapping("/edit/{idUsuario}")
    public String editCandidato(@PathVariable(name = "idUsuario") final String idUsuarioEncrypt,
                                @ModelAttribute("candidato") @Valid final CandidatoDTO candidatoDTO,
                                final BindingResult bindingResult,
                                @RequestParam(name = "file", required = false) MultipartFile imagen,
                                final RedirectAttributes redirectAttributes, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al editar el candidato.");
            return "candidato/edit";
        }

        Long idUsuario = candidatoDTO.getIdUsuario();

        // Verificar si se ha proporcionado una nueva imagen
        if (imagen != null && !imagen.isEmpty()) {
            try {
                // Eliminar la imagen anterior si existe
                if (candidatoDTO.getImagen() != null && !candidatoDTO.getImagen().isEmpty()) {
                    usuarioService.eliminarImagen(candidatoDTO.getImagen());
                }
                // Guardar la nueva imagen
                String rutaImagen = usuarioService.guardarImagen(imagen, idUsuario);
                candidatoDTO.setImagen(rutaImagen); // Actualizar la DTO con la nueva ruta
                session.setAttribute("imagen", rutaImagen);

            } catch (IOException e) {
                model.addAttribute(WebUtils.MSG_ERROR, "Error solo puedes guardar imagenes" );
                System.out.println(e);
                return "candidato/edit";
            }
        }
        // Actualizar los datos del candidato
        candidatoService.update(idUsuario, candidatoDTO);

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("candidato.update.success"));
        return "redirect:/candidatos/perfil";
    }



    @PostMapping("/delete/{idUsuario}")
    public String delete(@PathVariable(name = "idUsuario") final String idUsuarioEncrypt,
            final RedirectAttributes redirectAttributes) {
        
        Long idUsuario = encryptionService.decrypt(idUsuarioEncrypt);
        final ReferencedWarning referencedWarning = candidatoService.getReferencedWarning(idUsuario);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            candidatoService.delete(idUsuario);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("candidato.delete.success"));
        }
        return "redirect:/candidatos/perfil";
    }

}
