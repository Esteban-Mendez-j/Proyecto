package com.miproyecto.proyecto.controller;


import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.util.ReferencedWarning;
import com.miproyecto.proyecto.util.WebUtils;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.util.Optional;

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
    

    // codigo nuevo
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

    // nuevo login
    @PostMapping("/login")
    public String login(@ModelAttribute("usuario") @Valid final UsuarioDTO usuarioDTO,
                        final BindingResult bindingResult, 
                        final HttpSession session, 
                        final RedirectAttributes redirectAttributes,
                        Model model) {
                        
    
        // Verificar si el usuario es válido utilizando el servicio
        if (!usuarioService.esUsuarioValido(usuarioDTO.getCorreo(), usuarioDTO.getContrasena())) {
            // Si no es válido, redirigimos a la página de inicio con un mensaje de error
            model.addAttribute(WebUtils.MSG_ERROR, "Correo o contraseña incorrectos.");
            return "html/login";
        }
        
        // si es válido esto busca al usuario en la base de datos
        Optional<UsuarioDTO> usuarioOptional = usuarioService.getByCorreoAndContrasena(
                usuarioDTO.getCorreo(), usuarioDTO.getContrasena());
                
        if (usuarioOptional.isPresent()) {
            UsuarioDTO usuarioLogueado = usuarioOptional.get();
            // Guardamos el usuario en la sesión
            session.setAttribute("usuario", usuarioLogueado);
            // guardamos el tipo en la sesion 
            session.setAttribute("tipo", usuarioLogueado.getTipo());
            // guardamos la variable que verifica si hay un usuario registardo 
            session.setAttribute("login", true);
            // guardamos el nombre de la imagen
            session.setAttribute("imagen", usuarioLogueado.getImagen());

            return "redirect:/";
        }
    
        // Si el usuario no se encontró en la base de datos, redirige a la página de login
        redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR, "Usuario no encontrado.");
        return "redirect:/usuarios/login";
    }

    
    // Método para cerrar sesión
    @GetMapping("/cerrarSesion")
    public String logout(HttpSession session,Model model) {
        
        session.removeAttribute("login");
        session.removeAttribute("usuario");
        session.removeAttribute("tipo");
        session.removeAttribute("imagens");
        return "redirect:/";
    }


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
    public String edit(@PathVariable(name = "idUsuario") final Long idUsuario, final Model model) {
        model.addAttribute("usuario", usuarioService.get(idUsuario));
        return "usuario/edit";
    }

    @PostMapping("/edit/{idUsuario}")
    public String edit(@PathVariable(name = "idUsuario") final Long idUsuario,
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
    public String delete(@PathVariable(name = "idUsuario") final Long idUsuario,
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
