package com.miproyecto.proyecto.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.miproyecto.proyecto.model.MensajeDTO;
import com.miproyecto.proyecto.service.ChatService;
import com.miproyecto.proyecto.service.UsuarioService;

@Controller
public class ChatWebsocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatService chatService;
    @Autowired
    private UsuarioService usuarioService;

    @MessageMapping("/chats.sendMessage")
    public void sendPrivateMessage(MensajeDTO mensajeDTO) {
        String senderId = usuarioService.get(Long.parseLong(mensajeDTO.getSenderId())).getCorreo();
        String receiverId = usuarioService.get(Long.parseLong(mensajeDTO.getReceiverId())).getCorreo();;
        
        if (senderId == null || receiverId == null) {
            throw new SecurityException("El usuario no está autenticado");
        }     
        MensajeDTO mensajeGuardado = chatService.agregarMensajeAChat(mensajeDTO);

        messagingTemplate.convertAndSendToUser(
            senderId,
            "/queue/messages",
            mensajeGuardado
        );
        messagingTemplate.convertAndSendToUser(
            receiverId,
            "/queue/messages",
            mensajeGuardado
        );
    }
}
