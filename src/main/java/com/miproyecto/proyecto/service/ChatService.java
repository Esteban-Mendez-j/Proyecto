package com.miproyecto.proyecto.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.miproyecto.proyecto.domain.Chat;
import com.miproyecto.proyecto.domain.Mensaje;
import com.miproyecto.proyecto.model.ChatDTO;
import com.miproyecto.proyecto.model.MensajeDTO;
import com.miproyecto.proyecto.repos.ChatRepository;
import com.miproyecto.proyecto.repos.MensajeRepository;
import com.miproyecto.proyecto.util.NotFoundException;
import org.springframework.data.mongodb.core.query.*;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MensajeRepository mensajeRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;  
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private VacanteService vacanteService;
    @Autowired
    private MongoTemplate mongoTemplate;


    public ChatDTO crearChat(String candidatoId, String vacanteId, String empresaId) {
    
    Chat chat = chatRepository.findByVacanteIdAndCandidatoId(vacanteId, candidatoId)
                .orElse(null);

    if (chat == null) {
        chat = new Chat(); 
        chat.setEmpresaId(empresaId);  
        chat.setCandidatoId(candidatoId);
        chat.setVacanteId(vacanteId);
        chat.setHoraUltimoMensaje(LocalDateTime.now());
        chat.setIsActive(true);
        chat.setNombreCandidato(usuarioService.get(Long.parseLong(candidatoId)).getNombre());
        chat.setNombreEmpresa(usuarioService.get(Long.parseLong(empresaId)).getNombre());
        chat.setTituloVacante(vacanteService.get(0L,Long.parseLong(vacanteId)).getTitulo());
        chat = chatRepository.save(chat);     
    }
    if(!chat.getIsActive()){chat.setIsActive(true);}

    return mapToDTO(chat, new ChatDTO());
}


    public ChatDTO findById(String chatId) {
        return chatRepository.findById(chatId)
                    .map(chat -> mapToDTO(chat, new ChatDTO()))
                    .orElseThrow(NotFoundException::new);
    }
    

    public MensajeDTO agregarMensajeAChat(MensajeDTO mensajeDTO) {
        if (mensajeDTO.getContent() == null || mensajeDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido no puede estar vacío");
        }
    
        Chat chat = chatRepository.findById(mensajeDTO.getChatId())
                .orElseThrow(() -> new NotFoundException("Chat no encontrado"));
    
        // Verificar si el chat está activo antes de permitir agregar un mensaje
        if (!chat.getIsActive()) {
            throw new IllegalArgumentException("Este chat ha sido cerrado y no puede recibir más mensajes");
        }
    
        chat.setContentUltimoMensaje(mensajeDTO.getContent());
        chat.setHoraUltimoMensaje(LocalDateTime.now());
        chatRepository.save(chat);
    
        String receiverRole = mensajeDTO.getSenderRole().equalsIgnoreCase("empresa") ? "CANDIDATO" : "EMPRESA";
        mensajeDTO.setTime(LocalDateTime.now());
        mensajeDTO.setState("enviado");
        mensajeDTO.setReceiverRole(receiverRole);
        Mensaje mensaje = mensajeMapToEntity(mensajeDTO, new Mensaje());
        mensajeRepository.save(mensaje);
        return mensajeMapToDTO(mensaje, new MensajeDTO());
    }
    
    public List<MensajeDTO> obtenerMensajesDeChat(String chatId, Pageable pageable) {
        Page<Mensaje> page = mensajeRepository.findByChatIdOrderByTimeAsc(chatId, pageable);
        return page.getContent().stream()
                .map(mensaje -> mensajeMapToDTO(mensaje, new MensajeDTO()))
                .collect(Collectors.toList());
    }

    public ChatDTO findByVacanteIdAndCandidatoId(Long vacanteId, Long candidatoId) {
        Chat chat = chatRepository.findByVacanteIdAndCandidatoId(vacanteId, candidatoId)
            .orElse(null);
        if (chat == null) {
            return null;  
        }
        return mapToDTO(chat, new ChatDTO());
    }


    //listar todos los chats 
    public Map<String, Object> listarChatsPaginados(Pageable pageable) {
        Page<Chat> chatPage = chatRepository.findAll(pageable);
        Page<ChatDTO> chats = chatPage.map(chat -> mapToDTO(chat, new ChatDTO()));
        return mapResponse(chats, "chats");
    }

    
    public Map<String, Object> buscarChatsConFiltros(
        String userId, String tipoUsuario, Boolean activoFiltro, String search, Pageable pageable) {

        Query query = new Query();

        // Filtra por tipo de usuario
        if ("empresa".equalsIgnoreCase(tipoUsuario)) {
            query.addCriteria(Criteria.where("empresaId").is(userId));
        } else if ("candidato".equalsIgnoreCase(tipoUsuario)) {
            query.addCriteria(Criteria.where("candidatoId").is(userId));
        } else {
            throw new IllegalArgumentException("Tipo de usuario inválido");
        }

        // Filtra por estado activo/inactivo si se especifica
        if (activoFiltro != null) {
            query.addCriteria(Criteria.where("isActive").is(activoFiltro));
        }

        // Filtra por búsqueda en título vacante (ignore case)
        if (search != null && !search.isEmpty()) {
            query.addCriteria(Criteria.where("tituloVacante").regex(search, "i"));
        }

        // Conteo total para paginación
        long count = mongoTemplate.count(query, Chat.class);

        // Aplica paginación y orden (opcional)
        query.with(pageable);

        List<Chat> chats = mongoTemplate.find(query, Chat.class);

        // Mapear a DTOs
        List<ChatDTO> chatDTOs = chats.stream()
            .map(chat -> mapToDTO(chat, new ChatDTO()))
            .collect(Collectors.toList());

        // Crear la página con DTOs
        Page<ChatDTO> pageDto = new PageImpl<>(chatDTOs, pageable, count);

        return mapResponse(pageDto, "chats");
    }

    public void cambiarEstadoChat(String chatId, boolean nuevoEstado, String mensajeContent) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if(chat == null){return;}
       
        chat.setIsActive(nuevoEstado);
        chatRepository.save(chat);

        if (nuevoEstado) {
            mensajeContent= "chat abierto nuevamente";
        }
        String empresaId = usuarioService.get(Long.parseLong(chat.getEmpresaId())).getCorreo();
        String candidatoId = usuarioService.get(Long.parseLong(chat.getCandidatoId())).getCorreo();
            
        messagingTemplate.convertAndSendToUser(empresaId, "/queue/chat-change", mensajeContent);
        messagingTemplate.convertAndSendToUser(candidatoId, "/queue/chat-change", mensajeContent);
    }


    public ChatDTO mapToDTO(Chat chat, ChatDTO chatDTO) {
        chatDTO.setId(chat.getId());
        chatDTO.setEmpresaId(chat.getEmpresaId());
        chatDTO.setCandidatoId(chat.getCandidatoId());
        chatDTO.setVacanteId(chat.getVacanteId());
        chatDTO.setIsActive(chat.getIsActive());
        chatDTO.setContentUltimoMensaje(chat.getContentUltimoMensaje());
        chatDTO.setHoraUltimoMensaje(chat.getHoraUltimoMensaje());
        chatDTO.setNombreCandidato(chat.getNombreCandidato());
        chatDTO.setNombreEmpresa(chat.getNombreEmpresa());
        chatDTO.setTituloVacante(chat.getTituloVacante());
        return chatDTO;
    }

    public Chat mapToEntity(ChatDTO chatDTO, Chat chat) { 
        chat.setId(chatDTO.getId());
        chat.setEmpresaId(chatDTO.getEmpresaId());
        chat.setCandidatoId(chatDTO.getCandidatoId());
        chat.setVacanteId(chatDTO.getVacanteId());
        chat.setIsActive(chatDTO.getIsActive());
        chat.setHoraUltimoMensaje(chatDTO.getHoraUltimoMensaje());
        chat.setNombreCandidato(chatDTO.getNombreCandidato());
        chat.setNombreEmpresa(chatDTO.getNombreEmpresa());
        chat.setTituloVacante(chatDTO.getTituloVacante());
        return chat;
    }

    public Map<String,Object> mapResponse(Page<ChatDTO> pageableResponse, String nameList){
        Map<String,Object> response = new HashMap<>();
        response.put(nameList, pageableResponse.getContent());
        response.put("totalElements", pageableResponse.getTotalElements());
        response.put("pageActual", pageableResponse.getPageable());
        response.put("totalPage", pageableResponse.getTotalPages());
        return response;
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
