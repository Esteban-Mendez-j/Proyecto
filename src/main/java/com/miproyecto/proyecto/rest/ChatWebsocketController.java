package com.miproyecto.proyecto.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.miproyecto.proyecto.model.MensajeDTO;

@Controller
public class ChatWebsocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chats.sendMessage")
    public void sendPrivateMessage(MensajeDTO mensajeDTO) {
        
        System.out.println("#### mensaje:"+ mensajeDTO.getContent());
        // Enviar al receptor
        messagingTemplate.convertAndSendToUser(
            mensajeDTO.getReceiverId(),
            "/queue/messages",
            mensajeDTO
        );

        // (Opcional) Enviar al emisor también, para actualizar su pantalla
        messagingTemplate.convertAndSendToUser(
            mensajeDTO.getSenderId(),
            "/queue/messages",
            mensajeDTO
        );
    }
}
