// package com.miproyecto.proyecto.controller;

// import com.miproyecto.proyecto.model.CandidatoDTO;
// import com.miproyecto.proyecto.model.ValidationGroups;
// import com.miproyecto.proyecto.service.CandidatoService;
// import com.miproyecto.proyecto.service.EncryptionService;
// import com.miproyecto.proyecto.service.EstudioService;
// import com.miproyecto.proyecto.service.HistorialLaboralService;
// import com.miproyecto.proyecto.service.PostuladoService;
// import com.miproyecto.proyecto.service.UsuarioService;
// import com.miproyecto.proyecto.util.JwtUtils;
// import com.miproyecto.proyecto.util.ReferencedWarning;
// import com.miproyecto.proyecto.util.WebUtils;

// import jakarta.validation.Valid;
// import jakarta.validation.groups.Default;

// import java.io.IOException;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.validation.BindingResult;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.multipart.MultipartFile;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;



// @Controller
// @RequestMapping("/candidatos")
// public class CandidatoController {

//     @Autowired
//     private JwtUtils jwtUtils;

//     private final PostuladoService postuladoService;
//     private final CandidatoService candidatoService;
//     private final EncryptionService encryptionService;
//     private final HistorialLaboralService historialLaboralService;
//     private final EstudioService estudioService;
//     private final UsuarioService usuarioService;
    
//     public CandidatoController(PostuladoService postuladoService, CandidatoService candidatoService,
//             EncryptionService encryptionService, HistorialLaboralService historialLaboralService,
//             EstudioService estudioService, UsuarioService usuarioService) {
//         this.postuladoService = postuladoService;
//         this.candidatoService = candidatoService;
//         this.encryptionService = encryptionService;
//         this.historialLaboralService = historialLaboralService;
//         this.estudioService = estudioService;
//         this.usuarioService = usuarioService;
//     }

//     @GetMapping("/perfil")
//     public String mostrarPerfil( 
//             @RequestParam(name = "idUsuario", required = false) String idUsuarioEncrypt,
//             @RequestParam(name = "nPostulacion", required = false) String nPostulacionEncrypt,
//             Model model, HttpSession session) {
//         Long idUsuario; Long nPostulacion = null ;

//         if (idUsuarioEncrypt == null && nPostulacionEncrypt == null) {
//             // Sacamos el ID del usuario que inicia sesion
//             String jwtToken = (String) session.getAttribute("jwtToken");
//             DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
//             idUsuario = Long.parseLong(jwtUtils.extractUsername(decodedJWT));
//         } else{
//             idUsuario = encryptionService.decrypt(idUsuarioEncrypt);
//             nPostulacion = encryptionService.decrypt(nPostulacionEncrypt);

//             if (postuladoService.get(nPostulacion).getIdUsuario() != idUsuario && nPostulacion != null) {
//                 model.addAttribute("error", "No tienes Permiso para acceder");
//                 return "/postulado/list";
//             }
//         }
        
//         CandidatoDTO candidatoDTO = candidatoService.get(idUsuario);
//         if(candidatoDTO == null){return"redirect:/usuarios/login";}
        
//         model.addAttribute("estudios", estudioService.getEstudiosByIdUsuario(idUsuario));
//         model.addAttribute("historialLaboral", historialLaboralService.getHistorialByIdUsuario(idUsuario));
//         model.addAttribute("candidato", candidatoDTO);
//         return "candidato/perfil"; 
//     }

//     @GetMapping("/add")
//     public String add(@ModelAttribute("candidato") final CandidatoDTO candidatoDTO) {
//         return "candidato/registro";
//     }


//     @PostMapping("/add")
//     public String add(@ModelAttribute("candidato") @Valid final CandidatoDTO candidatoDTO,
//             final BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model) {
//         if (bindingResult.hasErrors()) {
//             return "candidato/registro";
//         }
//         candidatoService.create(candidatoDTO);
//         redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("candidato.create.success"));
//         return "redirect:/usuarios/login";
//     }

//     @GetMapping("/edit/{idUsuario}")
//     public String edit(@PathVariable(name = "idUsuario") final String idUsuarioEncrypt,
//             final Model model) {
//         Long idUsuario = encryptionService.decrypt(idUsuarioEncrypt);
//         model.addAttribute("candidato", candidatoService.get(idUsuario));
//         return "candidato/edit";
//     }

    
//     @PostMapping("/edit")
//     public String editCandidato(@ModelAttribute("candidato") 
//                                 @Validated({ValidationGroups.OnUpdate.class, Default.class}) 
//                                 final CandidatoDTO candidatoDTO,
//                                 final BindingResult bindingResult,
//                                 @RequestParam(name = "file", required = false) MultipartFile imagen,
//                                 final RedirectAttributes redirectAttributes, Model model) {
//         if (bindingResult.hasErrors()) {
//             model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al editar el candidato.");
//             return "candidato/edit";
//         }

//         Long idUsuario = candidatoDTO.getIdUsuario();

//         // Verificar si se ha proporcionado una nueva imagen
//         if (imagen != null && !imagen.isEmpty()) {
//             try {
//                 // Eliminar la imagen anterior si existe
//                 if (candidatoDTO.getImagen() != null && !candidatoDTO.getImagen().isEmpty()) {
//                     usuarioService.eliminarImagen(candidatoDTO.getImagen());
//                 }
//                 // Guardar la nueva imagen
//                 String rutaImagen = usuarioService.guardarImagen(imagen, idUsuario);
//                 candidatoDTO.setImagen(rutaImagen); // Actualizar la DTO con la nueva ruta

//             } catch (IOException e) {
//                 model.addAttribute(WebUtils.MSG_ERROR, "Error solo puedes guardar imagenes" );
//                 System.out.println(e);
//                 return "candidato/edit";
//             }
//         }
//         // Actualizar los datos del candidato
//         candidatoService.update(idUsuario, candidatoDTO);

//         redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("candidato.update.success"));
//         return "redirect:/candidatos/perfil";
//     }


    
//     @PostMapping("/edit/{idUsuario}")
//     public String editCandidato(@PathVariable(name = "idUsuario") final String idUsuarioEncrypt,
//                                 @ModelAttribute("candidato") @Valid final CandidatoDTO candidatoDTO,
//                                 final BindingResult bindingResult,
//                                 @RequestParam(name = "file", required = false) MultipartFile imagen,
//                                 final RedirectAttributes redirectAttributes, Model model, HttpSession session) {
//         if (bindingResult.hasErrors()) {
//             model.addAttribute(WebUtils.MSG_ERROR, "Hubo un problema al editar el candidato.");
//             return "candidato/edit";
//         }

//         Long idUsuario = candidatoDTO.getIdUsuario();

//         // Verificar si se ha proporcionado una nueva imagen
//         if (imagen != null && !imagen.isEmpty()) {
//             try {
//                 // Eliminar la imagen anterior si existe
//                 if (candidatoDTO.getImagen() != null && !candidatoDTO.getImagen().isEmpty()) {
//                     usuarioService.eliminarImagen(candidatoDTO.getImagen());
//                 }
//                 // Guardar la nueva imagen
//                 String rutaImagen = usuarioService.guardarImagen(imagen, idUsuario);
//                 candidatoDTO.setImagen(rutaImagen); // Actualizar la DTO con la nueva ruta
//                 session.setAttribute("imagen", rutaImagen);

//             } catch (IOException e) {
//                 model.addAttribute(WebUtils.MSG_ERROR, "Error solo puedes guardar imagenes" );
//                 System.out.println(e);
//                 return "candidato/edit";
//             }
//         }
//         // Actualizar los datos del candidato
//         candidatoService.update(idUsuario, candidatoDTO);

//         redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("candidato.update.success"));
//         return "redirect:/candidatos/perfil";
//     }



//     @PostMapping("/delete/{idUsuario}")
//     public String delete(@PathVariable(name = "idUsuario") final String idUsuarioEncrypt,
//             final RedirectAttributes redirectAttributes) {
        
//         Long idUsuario = encryptionService.decrypt(idUsuarioEncrypt);
//         final ReferencedWarning referencedWarning = candidatoService.getReferencedWarning(idUsuario);
//         if (referencedWarning != null) {
//             redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
//                     WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
//         } else {
//             candidatoService.delete(idUsuario);
//             redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("candidato.delete.success"));
//         }
//         return "redirect:/candidatos/perfil";
//     }

// }
