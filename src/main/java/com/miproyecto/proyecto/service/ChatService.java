package com.miproyecto.proyecto.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miproyecto.proyecto.domain.Chat;
import com.miproyecto.proyecto.domain.Mensaje;
import com.miproyecto.proyecto.model.ChatDTO;
import com.miproyecto.proyecto.model.MensajeDTO;
import com.miproyecto.proyecto.repos.ChatRepository;
import com.miproyecto.proyecto.repos.MensajeRepository;
import com.miproyecto.proyecto.util.NotFoundException;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    public ChatDTO crearChat(String empresaId, String candidatoId) {
        Chat chat = new Chat();
        chat.setId("123");
        chat.setEmpresaId(empresaId);
        chat.setCandidatoId(candidatoId);
        chat.setHoraUltimoMensaje(LocalDateTime.now());
        chat.setIsActive(true);
        // chat = chatRepository.save(chat);
        return mapToDTO(chat, new ChatDTO());
    }
    

    public MensajeDTO agregarMensajeAChat(String chatId, String senderId, String receiverId, String senderRole, String contenido) {
        if (contenido == null || contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido no puede estar vacío");
        }
    
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new NotFoundException("Chat no encontrado"));
    
        chat.setContentUltimoMensaje(contenido);
        chat.setHoraUltimoMensaje(LocalDateTime.now());
        chatRepository.save(chat);
    
        String receiverRole = senderRole.equalsIgnoreCase("empresa") ? "candidato" : "empresa";
    
        Mensaje mensaje = new Mensaje();
        mensaje.setChatId(chatId);
        mensaje.setSenderId(senderId);
        mensaje.setReceiverId(receiverId);
        mensaje.setSenderRole(senderRole.toLowerCase());
        mensaje.setReceiverRole(receiverRole.toLowerCase());
        mensaje.setContent(contenido);
        mensaje.setTime(LocalDateTime.now());
        mensaje.setState("enviado");
        mensajeRepository.save(mensaje);
    
        return mensajeMapToDTO(mensaje, new MensajeDTO());
    }
    

    // Listar mensajes de un chat
    public List<MensajeDTO> obtenerMensajesDeChat(String chatId) {
        List<Mensaje> Listmensajes = mensajeRepository.findByChatIdOrderByTimeAsc(chatId);
        return Listmensajes.stream()
                .map(mensaje -> mensajeMapToDTO(mensaje, new MensajeDTO()))
                .collect(Collectors.toList());
    }

    // Listar chats de una empresa
    public List<ChatDTO> listarChatsPorEmpresa(String empresaId) {
        List<Chat> chats = chatRepository.findByEmpresaId(empresaId);
        return chats.stream()
                .map(chat -> mapToDTO(chat, new ChatDTO()))
                .collect(Collectors.toList());
    }

    // Listar chats de un candidato
    public List<ChatDTO> listarChatsPorCandidato(String candidatoId) {
        List<Chat> chats = chatRepository.findByCandidatoId(candidatoId);
        return chats.stream()
                .map(chat -> mapToDTO(chat, new ChatDTO()))
                .collect(Collectors.toList());
    }

    // Cambiar estado del chat (por ejemplo, para cerrarlo)
    public void cambiarEstadoChat(String chatId, boolean nuevoEstado) {
        Chat chat = chatRepository.findById(chatId)
            .orElseThrow(NotFoundException::new);
        chat.setIsActive(nuevoEstado);
        chatRepository.save(chat);
    }

    public ChatDTO mapToDTO(Chat chat, ChatDTO chatDTO) {
        chatDTO.setId(chat.getId());
        chatDTO.setEmpresaId(chat.getEmpresaId());
        chatDTO.setCandidatoId(chat.getCandidatoId());
        chatDTO.setIsActive(chat.getIsActive());
        chatDTO.setContentUltimoMensaje(chat.getContentUltimoMensaje());
        chatDTO.setHoraUltimoMensaje(chat.getHoraUltimoMensaje());
        return chatDTO;
    }

    public Chat mapToEntity(ChatDTO chatDTO, Chat chat) { 
        chat.setId(chatDTO.getId());
        chat.setEmpresaId(chatDTO.getEmpresaId());
        chat.setCandidatoId(chatDTO.getCandidatoId());
        chat.setIsActive(chatDTO.getIsActive());
        chat.setHoraUltimoMensaje(chatDTO.getHoraUltimoMensaje());
        return chat;
    }

    public Mensaje mensajeMapToEntity(MensajeDTO mensajeDTO, Mensaje mensaje) { 
        mensaje.setChatId(mensajeDTO.getChatId());
        mensaje.setSenderId(mensajeDTO.getSenderId());
        mensaje.setReceiverId(mensajeDTO.getReceiverId());
        mensaje.setSenderRole(mensajeDTO.getSenderRole());
        mensaje.setReceiverRole(mensajeDTO.getReceiverRole());
        mensaje.setContent(mensajeDTO.getContent());
        mensaje.setTime(mensajeDTO.getTime());
        return mensaje;
    }

    public MensajeDTO mensajeMapToDTO(Mensaje mensaje, MensajeDTO mensajeDTO) {
        mensajeDTO.setChatId(mensaje.getChatId());
        mensajeDTO.setSenderId(mensaje.getSenderId());
        mensajeDTO.setReceiverId(mensaje.getReceiverId());
        mensajeDTO.setSenderRole(mensaje.getSenderRole());
        mensajeDTO.setReceiverRole(mensaje.getReceiverRole());
        mensajeDTO.setContent(mensaje.getContent());
        mensajeDTO.setTime(mensaje.getTime());
        mensajeDTO.setState(mensaje.getState());
        return mensajeDTO;
    }
}
