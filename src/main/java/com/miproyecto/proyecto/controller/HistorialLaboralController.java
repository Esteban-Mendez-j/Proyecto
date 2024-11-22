package com.miproyecto.proyecto.controller;

import com.miproyecto.proyecto.domain.Candidato;
import com.miproyecto.proyecto.model.HistorialLaboralDTO;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.repos.CandidatoRepository;
import com.miproyecto.proyecto.service.EncryptionService;
import com.miproyecto.proyecto.service.HistorialLaboralService;
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
@RequestMapping("/historialLaborals")
public class HistorialLaboralController {

    private final HistorialLaboralService historialLaboralService;
    private final CandidatoRepository candidatoRepository;
    private final EncryptionService encryptionService;

    public HistorialLaboralController(final HistorialLaboralService historialLaboralService,
            final CandidatoRepository candidatoRepository) {
        this.historialLaboralService = historialLaboralService;
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
        model.addAttribute("historialLaborals", historialLaboralService.findAll());
        return "historialLaboral/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("historialLaboral") final HistorialLaboralDTO historialLaboralDTO, 
            HttpSession session) {
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");
        historialLaboralDTO.setIdUsuario(usuario.getIdUsuario());
        return "historialLaboral/add";
    }

    @PostMapping("/add")
    public String add(
            @ModelAttribute("historialLaboral") @Valid final HistorialLaboralDTO historialLaboralDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al Registrar el Historial");
            return "historialLaboral/add";
        }
        historialLaboralService.create(historialLaboralDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("historialLaboral.create.success"));
        return "redirect:/candidatos/perfil";
    }

    @GetMapping("/edit/{iDHistorial}")
    public String edit(@PathVariable(name = "iDHistorial") final String iDHistorialEncrypt,
            final Model model) {
        Long iDHistorial = encryptionService.decrypt(iDHistorialEncrypt);
        model.addAttribute("historialLaboral", historialLaboralService.get(iDHistorial));
        return "historialLaboral/edit";
    }

    @PostMapping("/edit/{iDHistorial}")
    public String edit(@PathVariable(name = "iDHistorial") final String iDHistorialEncrypt,
            @ModelAttribute("historialLaboral") @Valid final HistorialLaboralDTO historialLaboralDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al Editar el Historial");
            return "historialLaboral/edit";
        }
        Long iDHistorial = encryptionService.decrypt(iDHistorialEncrypt);
        historialLaboralService.update(iDHistorial, historialLaboralDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("historialLaboral.update.success"));
        return "redirect:/candidatos/perfil";
    }

    @PostMapping("/delete/{iDHistorial}")
    public String delete(@PathVariable(name = "iDHistorial") final String iDHistorialEncrypt,
            final RedirectAttributes redirectAttributes) {
        Long iDHistorial = encryptionService.decrypt(iDHistorialEncrypt);
        historialLaboralService.delete(iDHistorial);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("historialLaboral.delete.success"));
        return "redirect:/candidatos/perfil";
    }

}
