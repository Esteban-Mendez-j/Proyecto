package com.miproyecto.proyecto.controller;


import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.util.ReferencedWarning;
import com.miproyecto.proyecto.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(final UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuario/list";
    }
    
    @GetMapping("/terminoYcondiciones")
    public String terminosCondiciones() {
        return "html/terminos-condiciones";
    }

    @GetMapping("/registro")
    public String Registro() {
        return "html/seleccion-registro";
    }

    @GetMapping("/login")
    public String login(@ModelAttribute("usuario") final UsuarioDTO usuarioDTO) {
        return "html/login";
    }

    @GetMapping("/login/error")
    public String loginError(Model model, HttpServletRequest request) {
        String correo = (String) request.getSession().getAttribute("LOGIN_EMAIL");
        String mensajeError = (String) request.getSession().getAttribute("LOGIN_ERROR_MESSAGE");
        Boolean isBanned = (Boolean) request.getSession().getAttribute("IS_BANNED");
        if (isBanned) {
            //tambine se puede por la id
            model.addAttribute("correo", correo);
            model.addAttribute("mensajeBan", mensajeError);
            request.getSession().removeAttribute("LOGIN_EMAIL");
            request.getSession().removeAttribute("LOGIN_ERROR_MESSAGE");
            request.getSession().removeAttribute("IS_BANNED");
            return "apelacion/EstadoCuenta"; 
        }

        model.addAttribute(WebUtils.MSG_ERROR, WebUtils.getMessage(mensajeError));
        return "html/login"; 
    }


    // @RequestMapping("/usuarios/cerrarSesion")
    // public String logout(@RequestParam(name = "tipo", defaultValue = "1") int tipo, RedirectAttributes redirectAttributes) {
    //     // Verificar el valor del parámetro 'tipo'
    //     if (tipo == 1) {
    //         redirectAttributes.addFlashAttribute("tipo", 1); // Tipo 1, por ejemplo sesión cerrada correctamente
    //     } else {
    //         redirectAttributes.addFlashAttribute("tipo", 2); // Tipo 2, por ejemplo, sesión expirada
    //     }
    //     return "redirect:/?logout"; // Redirige a la página de inicio con el parámetro logout
    // }



    @GetMapping("/add")
    public String add(@ModelAttribute("usuario") final UsuarioDTO usuarioDTO) {
        return "usuario/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("usuario") @Valid final UsuarioDTO usuarioDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "usuario/add";
        }
        usuarioService.create(usuarioDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("usuario.create.success"));
        return "redirect:/usuarios";
    }

    @GetMapping("/edit/{idUsuario}")
    public String edit(@PathVariable final Long idUsuario, final Model model) {
        model.addAttribute("usuario", usuarioService.get(idUsuario));
        return "usuario/edit";
    }

    @PostMapping("/edit/{idUsuario}")
    public String edit(@PathVariable final Long idUsuario,
            @ModelAttribute("usuario") @Valid final UsuarioDTO usuarioDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "usuario/edit";
        }
        usuarioService.update(idUsuario, usuarioDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("usuario.update.success"));
        return "redirect:/usuarios";
    }

    @PostMapping("/delete/{idUsuario}")
    public String delete(@PathVariable final Long idUsuario,
            final RedirectAttributes redirectAttributes) {
        final ReferencedWarning referencedWarning = usuarioService.getReferencedWarning(idUsuario);
        if (referencedWarning != null) {
            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
                    WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
        } else {
            usuarioService.delete(idUsuario);
            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("usuario.delete.success"));
        }
        return "redirect:/usuarios";
    }

}
