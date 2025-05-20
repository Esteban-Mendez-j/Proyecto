package com.miproyecto.proyecto.rest;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.miproyecto.proyecto.model.ChatDTO;
import com.miproyecto.proyecto.model.MensajeDTO;
import com.miproyecto.proyecto.model.UsuarioDTO;
import com.miproyecto.proyecto.model.VacanteDTO;
import com.miproyecto.proyecto.service.ChatService;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.service.VacanteService;
import com.miproyecto.proyecto.util.JwtUtils;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/chats", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatResource {

    @Autowired
    private ChatService chatService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private VacanteService vacanteService;
  
   // Crear un nuevo chat
    @PostMapping("/crear")
    public ResponseEntity<ChatDTO> crearChat(@RequestBody ChatDTO response) {
        // Verificar que la empresa exista y sea un usuario tipo "empresa"
        VacanteDTO vacantesDTO =vacanteService.get(0L,Long.parseLong(response.getVacanteId()));

        UsuarioDTO empresa = usuarioService.get(vacantesDTO.getIdUsuario());
        
        if (!empresa.getRoles().contains("EMPRESA")) {
            throw new IllegalArgumentException("Solo las empresas pueden crear chats");
        }
        UsuarioDTO candidato = usuarioService.get(Long.parseLong(response.getCandidatoId()));

        // Verificar que el candidato exista
        if (candidato == null) {
            throw new IllegalArgumentException("No existe este candidato");
        }

        if(!candidato.getIsActive()){
            ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body("Error al iniciar chat, El usaurio esta baneado");
        }
        
        ChatDTO chatDTO = chatService.crearChat(response.getCandidatoId(),response.getVacanteId(), empresa.getIdUsuario().toString());
        return ResponseEntity.ok(chatDTO);
    }

    // Agregar un mensaje a un chat
    @PostMapping("/{chatId}/mensajes")
    public ResponseEntity<MensajeDTO> agregarMensaje(@PathVariable String chatId,@RequestBody MensajeDTO mensajeDTO) {
        mensajeDTO.setChatId(chatId);
        MensajeDTO mensajeDTOResponse = chatService.agregarMensajeAChat(mensajeDTO);
        return ResponseEntity.ok(mensajeDTOResponse);
    }

    // Obtener información del chat por chatId
    @GetMapping("/{chatId}/info")
    public ResponseEntity<Map<String, Object>> obtenerInfoChat(
        @PathVariable String chatId, 
        @CookieValue(name="jwtToken") String jwtToken) {
        
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("chatInfo",chatService.findById(chatId));
        responseData.put("userId",jwtUtils.extractUsername(decodedJWT) );
        responseData.put("rolPrincipal", decodedJWT.getClaim("rolPrincipal").asString());
        return ResponseEntity.ok(responseData);
    }

    //mensajes de un chat
    @GetMapping("/{chatId}/mensajes")
    public ResponseEntity<List<MensajeDTO>> listarMensajes(@PathVariable String chatId, Pageable pageable) {
        List<MensajeDTO> mensajes = chatService.obtenerMensajesDeChat(chatId, pageable);
        return ResponseEntity.ok(mensajes);
    }

    // Listar chats todos
    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listarChats(
        @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Map<String, Object> response = chatService.listarChatsPaginados(pageable);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{tipoUsuario}/{userId}")
    public ResponseEntity<Map<String, Object>> listarChatsPorUsuario(
        @PathVariable String tipoUsuario,        // "empresa" o "candidato"
        @PathVariable String userId,
        @RequestParam(name = "estado",required = false) String estado,
        @RequestParam(name = "search" , required = false) String search,
        @PageableDefault(page = 0, size = 10) Pageable pageable) {

        Boolean activoFiltro = null;
        if ("activos".equalsIgnoreCase(estado)) {
            activoFiltro = true;
        } else if ("inactivos".equalsIgnoreCase(estado)) {
            activoFiltro = false;
        }
        Map<String, Object> response = chatService.buscarChatsConFiltros(userId,tipoUsuario,
        activoFiltro,search,pageable);

        return ResponseEntity.ok(response);
    }



    @PatchMapping("/{chatId}/estado")
    public ResponseEntity<Void> cambiarEstadoChat(@PathVariable String chatId, @RequestParam boolean isActive, HttpSession session) {
        // Verificar que el chat existe
        String jwtToken = (String) session.getAttribute("jwtToken");
        DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
        String idUsuario = jwtUtils.extractUsername(decodedJWT);

        ChatDTO chat = chatService.findById(chatId);

        // Verificar que quien está cambiando el estado es la empresa que creó el chat
        if (!chat.getEmpresaId().equals(idUsuario)) {
            throw new IllegalArgumentException("Solo la empresa que creó el chat puede cambiar su estado");
        }

        chatService.cambiarEstadoChat(chatId, isActive, "El chat fue cerrado por la empresa");
        return ResponseEntity.noContent().build();
    }

}

