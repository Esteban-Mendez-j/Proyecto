package com.miproyecto.proyecto.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.EmpresaDTO;
import com.miproyecto.proyecto.repos.UsuarioRepository;
import com.miproyecto.proyecto.service.EmpresaService;
import com.miproyecto.proyecto.service.EncryptionService;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.service.VacanteService;
import com.miproyecto.proyecto.util.JwtUtils;
import com.miproyecto.proyecto.util.ReferencedWarning;
import com.miproyecto.proyecto.util.WebUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.io.IOException;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/empresas")
public class EmpresaController {

    @Autowired
    private JwtUtils jwtUtils;

    private final EmpresaService empresaService;
    private final EncryptionService encryptionService;
    private final UsuarioService usuarioService;
    private final VacanteService vacanteService;

    public EmpresaController(final EmpresaService empresaService,
            final UsuarioRepository usuarioRepository,
            final UsuarioService usuarioService, 
            final VacanteService vacanteService) {
        this.empresaService = empresaService;
        this.encryptionService = new EncryptionService();
        this.usuarioService = usuarioService;
        this.vacanteService = vacanteService;
    }


    // @GetMapping("/perfil")
    // public String mostrarPerfil( Model model,HttpSession session,
    //         @RequestParam(value = "idUsuario", required = false) String idUsuarioEncrypt) {        
    //     Long idUsuario;
    //     if (idUsuarioEncrypt == null) {
    //         // Sacamos el ID del usuario que inicia sesion
    //         String jwtToken = (String) session.getAttribute("jwtToken");
    //         DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
    //         idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
    //     } else{
    //         idUsuario = encryptionService.decrypt(idUsuarioEncrypt);
    //     }

    //     EmpresaDTO empresaDTO = empresaService.get(idUsuario);        
    //     if (empresaDTO == null) {return "redirect:/usuarios/login";} 
    //     model.addAttribute("vacantes", vacanteService.findByIdUsuario(idUsuario));
    //     model.addAttribute("empresa", empresaDTO);
    //     return "empresa/perfil"; 
    // }


    @GetMapping("/add")
    public String add(@ModelAttribute("empresa") final EmpresaDTO empresaDTO) {
        return "empresa/registro";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("empresa") @Valid final EmpresaDTO empresaDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al registrar el candidato.");
            return "empresa/registro";
        }    
        empresaService.create(empresaDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("empresa.create.success"));
        return "redirect:/usuarios/login";
    }

    @GetMapping("/edit/{idUsuario}")
    public String edit(@PathVariable(name = "idUsuario") final String idUsuarioEncrypt, final Model model) {
        Long idUsuario = encryptionService.decrypt(idUsuarioEncrypt);
        model.addAttribute("empresa", empresaService.get(idUsuario));
        return "empresa/edit";
    }

    @PostMapping("/edit/{idUsuario}")
    public String editempresa(@PathVariable(name = "idUsuario") final String idUsuarioEncrypt,
                                @ModelAttribute("empresa") @Valid final EmpresaDTO empresaDTO,
                                final BindingResult bindingResult,
                                @RequestParam(name = "file", required = false) MultipartFile imagen,
                                final RedirectAttributes redirectAttributes, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al editar la empresa");
            return "empresa/edit";
        }
        
        Long idUsuario = empresaDTO.getIdUsuario();

        // Verificar si se ha proporcionado una nueva imagen
        if (imagen != null && !imagen.isEmpty()) {
            try {
                // Eliminar la imagen anterior si existe
                if (empresaDTO.getImagen() != null && !empresaDTO.getImagen().isEmpty()) {
                    usuarioService.eliminarImagen(empresaDTO.getImagen());
                }
                // Guardar la nueva imagen
                String rutaImagen = usuarioService.guardarImagen(imagen, idUsuario);
                empresaDTO.setImagen(rutaImagen); // Actualizar la DTO con la nueva ruta
                session.setAttribute("imagen", rutaImagen); // actualizamos la ruta en la sesion

            } catch (IOException e) {
                model.addAttribute(WebUtils.MSG_ERROR, "Error al guardar la imagen: " + e.getMessage());
                return "empresa/edit";
            }
        }
        // Actualizar los datos del empresa
        empresaService.update(idUsuario, empresaDTO);

        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("empresa.update.success"));
        return "redirect:/empresas/perfil";
    }


    @PostMapping("/delete/{idUsuario}")
    public String delete(@PathVariable(name = "idUsuario") final String idUsuarioEncrypt,
            final RedirectAttributes redirectAttributes) {
        
        Long idUsuario = encryptionService.decrypt(idUsuarioEncrypt);
        final ReferencedWarning referencedWarning = empresaService.getReferencedWarning(idUsuario);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            empresaService.delete(idUsuario);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("empresa.delete.success"));
        }
        return "redirect:/";
    }

}
