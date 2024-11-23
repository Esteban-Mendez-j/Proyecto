package com.miproyecto.proyecto.controller;

import com.miproyecto.proyecto.domain.Empresa;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.model.VacanteDTO;
import com.miproyecto.proyecto.repos.EmpresaRepository;
import com.miproyecto.proyecto.service.EncryptionService;
import com.miproyecto.proyecto.service.VacanteService;
import com.miproyecto.proyecto.util.CustomCollectors;
import com.miproyecto.proyecto.util.ReferencedWarning;
import com.miproyecto.proyecto.util.WebUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/vacantes")
public class VacanteController {

    private final VacanteService vacanteService;
    private final EmpresaRepository empresaRepository;
    private final EncryptionService encryptionService;

    public VacanteController(final VacanteService vacanteService,
            final EmpresaRepository empresaRepository) {
        this.vacanteService = vacanteService;
        this.empresaRepository = empresaRepository;
        this.encryptionService = new EncryptionService();
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("idUsuarioValues", empresaRepository.findAll(Sort.by("idUsuario"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Empresa::getIdUsuario, Empresa::getIdUsuario)));
    }

    

    @GetMapping
    public String list(final Model model,HttpSession session) {
        // aqui debe ir el id de la session iniciada solo si es empresa
        UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");

        Long idUsuario = usuario.getIdUsuario();
        model.addAttribute("vacantes", vacanteService.findByIdUsuario(idUsuario));
        return "vacante/list";
    }
    

    @GetMapping("/listar")
    public String listarVacantes(
            @ModelAttribute("filtro") VacanteDTO filtro, Model model, HttpSession session,
            @RequestParam(name = "titulo", required = false) String titulo) {
    
        // Guardar el filtro en la sesión
        session.setAttribute("filtro", filtro);
    
        // Buscar vacantes con el filtro aplicado
        List<VacanteDTO> vacantes;
        if (filtro != null && 
            ( (filtro.getCargo() != null && !filtro.getCargo().isEmpty()) || 
            (filtro.getCiudad() != null && !filtro.getCiudad().isEmpty()) || 
            (filtro.getTipo() != null && !filtro.getTipo().isEmpty()) || 
            (filtro.getModalidad() != null && !filtro.getModalidad().isEmpty()) || 
            (filtro.getTitulo() != null && !filtro.getTitulo().isEmpty()) )) {
            vacantes = vacanteService.buscarVacantesConFiltros(filtro);
        } else {
            vacantes = vacanteService.findAll();
        }


        // Seleccionar la primera vacante si hay alguna
        if (!vacantes.isEmpty()) {
            Long nvacantes = vacantes.get(0).getNvacantes(); 
            model.addAttribute("vacanteSeleccionada", vacanteService.get(nvacantes));
        }
    
        model.addAttribute("vacantes", vacantes);
        model.addAttribute("filtro", filtro);
    
        // Guardar el filtro actualizado en la sesión para futuras búsquedas
        session.setAttribute("filtro", filtro);
    
        return "html/ofertas";
    }


    @GetMapping("/seleccion/{nvacantes}")
    public String seleccionVacante(@PathVariable(name = "nvacantes") String nvacantesEncrypt, Model model,HttpSession session) {
        
        // desencritamos el nvacante
        long nvacantes= encryptionService.decrypt(nvacantesEncrypt);

        // Buscar la vacante seleccionada por su ID desencritadad
        VacanteDTO vacanteSeleccionada = vacanteService.get(nvacantes);

        VacanteDTO filtro = (VacanteDTO) session.getAttribute("filtro");
        // Buscar vacantes con el filtro aplicado
        List<VacanteDTO> vacantes = vacanteService.findAll();
        
        model.addAttribute("vacantes", vacantes);
        // Si no se encuentra la vacante, se podría redirigir a la lista de vacantes
        if (vacanteSeleccionada == null) {
            return "redirect:/vacantes/listar"; // Redirigir a la lista de vacantes
        }

        model.addAttribute("filtro", filtro);
        model.addAttribute("vacanteSeleccionada", vacanteSeleccionada);
        return "html/ofertas";
    }

    @GetMapping("/eliminar/filtro")
    public String eliminaFiltro(HttpSession session, Model model) {
        session.setAttribute("filtro", null);
        model.addAttribute("filtro", null);
        return "redirect:/vacantes/listar";
    }
    
    
    @GetMapping("/add")
    public String add(@ModelAttribute("vacante") final VacanteDTO vacanteDTO, 
            HttpSession session,
            @RequestParam("Tipo") String tipo, Model model) {
        final UsuarioDTO usuario = (UsuarioDTO) session.getAttribute("usuario");
        vacanteDTO.setIdUsuario(usuario.getIdUsuario());
        vacanteDTO.setTipo(tipo);
        model.addAttribute("Tipo", tipo);
        return "vacante/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("vacante") @Valid final VacanteDTO vacanteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al registrar la Vacante");
            return "vacante/add";
        }
        vacanteDTO.setFechaPublicacion(LocalDate.now());
        vacanteService.create(vacanteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("vacante.create.success"));
        return "redirect:/vacantes";
    }

    @GetMapping("/edit/{nvacantes}")
    public String edit(@PathVariable(name = "nvacantes") final String nvacantesEncrypt, final Model model) {
        // desencritamos el nvacante
        long nvacantes= encryptionService.decrypt(nvacantesEncrypt);
        model.addAttribute("vacante", vacanteService.get(nvacantes));
        return "vacante/edit";
    }

    @PostMapping("/edit/{nvacantes}")
    public String edit(@PathVariable(name = "nvacantes") final String nvacantesEncrypt,
            @ModelAttribute("vacante") @Valid final VacanteDTO vacanteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al Editar la Vacante");
            return "vacante/edit";
        }
        // desencritamos el nvacante
        long nvacantes= encryptionService.decrypt(nvacantesEncrypt);
        
        vacanteService.update(nvacantes, vacanteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("vacante.update.success"));
        return "redirect:/vacantes";
    }

    @PostMapping("/delete/{nvacantes}")
    public String delete(@PathVariable(name = "nvacantes") final String nvacantesEncrypt,
            final RedirectAttributes redirectAttributes) {
        // desencritamos el nvacante
        long nvacantes= encryptionService.decrypt(nvacantesEncrypt);

        final ReferencedWarning referencedWarning = vacanteService.getReferencedWarning(nvacantes);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            vacanteService.delete(nvacantes);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("vacante.delete.success"));
        }
        return "redirect:/vacantes";
    }

}
