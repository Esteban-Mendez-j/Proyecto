package com.miproyecto.proyecto.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.miproyecto.proyecto.model.MensajeDTO;
import com.miproyecto.proyecto.service.ChatService;

@Controller
public class ChatWebsocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private ChatService chatService;

    @MessageMapping("/chats.sendMessage")
    public void sendPrivateMessage(MensajeDTO mensajeDTO) {
        // Validar que el emisor está autenticado y tiene permisos
        String senderId = mensajeDTO.getSenderId();
        String receiverId = mensajeDTO.getReceiverId();
        
        if (senderId == null || receiverId == null) {
            throw new SecurityException("El usuario no está autenticado");
        }     
        // Guardar el mensaje y actualizar el chat
        MensajeDTO mensajeGuardado = chatService.agregarMensajeAChat(mensajeDTO);
        // (Opcional) Enviar al emisor también, para actualizar su pantalla
        messagingTemplate.convertAndSendToUser(
            mensajeGuardado.getSenderId(),
            "/queue/messages",
            mensajeGuardado
        );

        // Enviar al receptor
        messagingTemplate.convertAndSendToUser(
            mensajeGuardado.getReceiverId(),
            "/queue/messages",
            mensajeGuardado
        );

        
    }


    @MessageMapping("/chats.closeChat")
    public void closeChat(MensajeDTO mensajeDTO) {
        // Lógica para manejar la desconexión de chat, cerrando o bloqueando el chat
        chatService.cambiarEstadoChat(mensajeDTO.getChatId(), false);
        
        // Notificar a ambos usuarios
        messagingTemplate.convertAndSendToUser(
                mensajeDTO.getReceiverId(),
                "/queue/messages",
                "El chat ha sido cerrado"
        );
        messagingTemplate.convertAndSendToUser(
                mensajeDTO.getSenderId(),
                "/queue/messages",
                "El chat ha sido cerrado"
        );
    }

}
