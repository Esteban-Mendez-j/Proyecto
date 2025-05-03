package com.miproyecto.proyecto.rest;

import com.miproyecto.proyecto.model.ChatDTO;
import com.miproyecto.proyecto.model.MensajeDTO;
import com.miproyecto.proyecto.service.ChatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/chats", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatResource {

    @Autowired
    private ChatService chatService;
  
    // Crear un nuevo chat
    @PostMapping("/crear")
    public ResponseEntity<ChatDTO> crearChat(@RequestBody ChatDTO response) {
        ChatDTO chatDTO = chatService.crearChat(response.getEmpresaId(), response.getCandidatoId());
        return ResponseEntity.ok(chatDTO);
    }

    // Agregar un mensaje a un chat
    @PostMapping("/{chatId}/mensajes")
    public ResponseEntity<MensajeDTO> agregarMensaje(@PathVariable String chatId,
                                                      @RequestParam String senderId,
                                                      @RequestParam String receiverId,
                                                      @RequestParam String senderRole,
                                                      @RequestBody String contenido) {
        MensajeDTO mensajeDTO = chatService.agregarMensajeAChat(chatId, senderId, receiverId, senderRole, contenido);
        return ResponseEntity.ok(mensajeDTO);
    }

    // Listar mensajes de un chat
    @GetMapping("/{chatId}/mensajes")
    public ResponseEntity<List<MensajeDTO>> listarMensajes(@PathVariable String chatId) {
        List<MensajeDTO> mensajes = chatService.obtenerMensajesDeChat(chatId);
        return ResponseEntity.ok(mensajes);
    }

    // Listar chats por empresa
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ChatDTO>> listarChatsEmpresa(@PathVariable String empresaId) {
        List<ChatDTO> chats = chatService.listarChatsPorEmpresa(empresaId);
        return ResponseEntity.ok(chats);
    }

    // Listar chats por candidato
    @GetMapping("/candidato/{candidatoId}")
    public ResponseEntity<List<ChatDTO>> listarChatsCandidato(@PathVariable String candidatoId) {
        List<ChatDTO> chats = chatService.listarChatsPorCandidato(candidatoId);
        return ResponseEntity.ok(chats);
    }

    // Cambiar el estado de un chat
    @PatchMapping("/{chatId}/estado")
    public ResponseEntity<Void> cambiarEstadoChat(@PathVariable String chatId, @RequestParam boolean isActive) {
        chatService.cambiarEstadoChat(chatId, isActive);
        return ResponseEntity.noContent().build();
    }
}

