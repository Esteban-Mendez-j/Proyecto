package com.miproyecto.proyecto.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.ApelacionDTO;
import com.miproyecto.proyecto.service.ApelacionService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/apelaciones")
public class ApelacionController {

    @Autowired
    private ApelacionService apelacionService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/list")
    public String list(Model model, HttpSession session) {
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        // Aquí puedes agregar lógica para filtrar las apelaciones dependiendo del rol del usuario
        model.addAttribute("apelaciones", apelacionService.findByUsuarioId(idUsuario));
        return "apelacion/list"; // Vista de lista de apelaciones
    }

    @GetMapping("/admin/list")
    public String listAdmin(Model model, HttpSession session) {
        model.addAttribute("apelaciones", apelacionService.findAll()); 
        return "apelacion/adminList"; 
    }


    @GetMapping("/add")
    public String add(@ModelAttribute("apelacion") final ApelacionDTO apelacionDTO, HttpSession session) {
        // Obtenemos el ID del usuario logueado
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        Long idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));

        apelacionDTO.setIdUsuario(idUsuario);
        return "apelacion/add"; // Vista para agregar una apelación
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("apelacion") @Valid final ApelacionDTO apelacionDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Hubo un error al agregar la apelación.");
            return "apelacion/add";
        }
        
        apelacionService.create(apelacionDTO); 
        redirectAttributes.addFlashAttribute("success", "La apelación se ha creado correctamente.");
        return "redirect:/apelacion/list";
    }

    @GetMapping("/edit/{idApelacion}")
    public String edit(@PathVariable("idApelacion") final Long idApelacion, Model model) {
        ApelacionDTO apelacionDTO = apelacionService.get(idApelacion);
        
        if (apelacionDTO == null) {
            model.addAttribute("error", "La apelación no existe.");
            return "redirect:/apelacion/list"; // Si no se encuentra la apelación, redirigimos a la lista
        }
        
        model.addAttribute("apelacion", apelacionDTO);
        return "apelacion/edit"; // Vista para editar la apelación
    }

    @PostMapping("/edit/{idApelacion}")
    public String edit(@PathVariable("idApelacion") final Long idApelacion, 
            @ModelAttribute("apelacion") @Valid final ApelacionDTO apelacionDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Hubo un error al editar la apelación.");
            return "apelacion/edit";
        }

        apelacionService.update(idApelacion, apelacionDTO); // Actualizamos la apelación
        redirectAttributes.addFlashAttribute("success", "La apelación se ha actualizado correctamente.");
        return "redirect:/apelaciones/list"; // Redirigimos a la lista de apelaciones
    }

    @PostMapping("/delete/{idApelacion}")
    public String delete(@PathVariable("idApelacion") final Long idApelacion, final RedirectAttributes redirectAttributes) {
        apelacionService.delete(idApelacion); // Eliminamos la apelación
        redirectAttributes.addFlashAttribute("info", "La apelación se ha eliminado correctamente.");
        return "redirect:/apelaciones/list"; // Redirigimos a la lista de apelaciones
    }

    @GetMapping("/cambiarEstado/{idApelacion}")
    public String cambiarEstado(@PathVariable("idApelacion") final Long idApelacion, Model model) {
        ApelacionDTO apelacionDTO = apelacionService.get(idApelacion);

        if (apelacionDTO == null) {
            model.addAttribute("error", "La apelación no existe.");
            return "redirect:/apelaciones/list"; // Si no existe la apelación, redirigimos
        }

        model.addAttribute("apelacion", apelacionDTO);
        return "apelacion/cambiarEstado"; // Vista para cambiar el estado de la apelación
    }

    @PostMapping("/cambiarEstado/{idApelacion}")
    public String cambiarEstado(@PathVariable("idApelacion") final Long idApelacion,
            @RequestParam("estado") final String estado, RedirectAttributes redirectAttributes) {

        ApelacionDTO apelacionDTO = apelacionService.get(idApelacion);

        if (apelacionDTO == null) {
            redirectAttributes.addFlashAttribute("error", "La apelación no existe.");
            return "redirect:/apelaciones/list"; // Si no existe la apelación, redirigimos
        }

        // Cambiamos el estado de la apelación
        apelacionDTO.setEstado(estado);
        apelacionService.update(idApelacion, apelacionDTO);
        redirectAttributes.addFlashAttribute("success", "El estado de la apelación ha sido actualizado correctamente.");
        return "redirect:/apelaciones/list"; // Redirigimos a la lista de apelaciones
    }
}

    

