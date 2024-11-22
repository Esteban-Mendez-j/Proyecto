package com.miproyecto.proyecto.controller;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.model.EstudioDTO;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.service.EncryptionService;
import com.miproyecto.proyecto.service.EstudioService;
import com.miproyecto.proyecto.util.CustomCollectors;
import com.miproyecto.proyecto.util.WebUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/estudios")
public class EstudioController {

    private final EstudioService estudioService;
    private final CandidatoRepository candidatoRepository;
    private final EncryptionService encryptionService;

    public EstudioController(final EstudioService estudioService,
            final CandidatoRepository candidatoRepository) {
        this.estudioService = estudioService;
        this.candidatoRepository = candidatoRepository;
        this.encryptionService = new EncryptionService();
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("idUsuarioValues", candidatoRepository.findAll(Sort.by("idUsuario"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Candidato::getIdUsuario, Candidato::getIdUsuario)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("estudios", estudioService.findAll());
        return "estudio/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("estudio") final EstudioDTO estudioDTO, HttpSession session) {
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");
        estudioDTO.setIdUsuario(usuario.getIdUsuario());
        return "estudio/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("estudio") @Valid final EstudioDTO estudioDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al Registrar el Estudio");
            return "estudio/add";
        }
        estudioService.create(estudioDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("estudio.create.success"));
        return "redirect:/candidatos/perfil";
    }

    @GetMapping("/edit/{idEstudio}")
    public String edit(@PathVariable(name = "idEstudio") final String idEstudioEncrypt, final Model model) {
        Long idEstudio = encryptionService.decrypt(idEstudioEncrypt);
        model.addAttribute("estudio", estudioService.get(idEstudio));
        return "estudio/edit";
    }

    @PostMapping("/edit/{idEstudio}")
    public String edit(@PathVariable(name = "idEstudio") final String idEstudioEncrypt,
            @ModelAttribute("estudio") @Valid final EstudioDTO estudioDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes,Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al editar el Estudio");
            return "estudio/edit";
        }
        Long idEstudio = encryptionService.decrypt(idEstudioEncrypt);
        estudioService.update(idEstudio, estudioDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("estudio.update.success"));
        return "redirect:/candidatos/perfil";
    }

    @PostMapping("/delete/{idEstudio}")
    public String delete(@PathVariable(name = "idEstudio") final String idEstudioEncrypt,
            final RedirectAttributes redirectAttributes) {
        Long idEstudio = encryptionService.decrypt(idEstudioEncrypt);
        estudioService.delete(idEstudio);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("estudio.delete.success"));
        return "redirect:/candidatos/perfil";
    }

}
