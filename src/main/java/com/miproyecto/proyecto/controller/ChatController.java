package com.miproyecto.proyecto.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.miproyecto.proyecto.model.Mensaje;

@Controller
public class ChatController {
    
    @MessageMapping("/chat1")
    @SendTo("/topic/canal1")
    public void getMensaje(Mensaje mensaje ){
        System.out.println("mensaje"+ mensaje);
    }
}
