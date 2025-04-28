package com.miproyecto.proyecto.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chats")
public class Chat {

    @Id
    private String id; // Este será tu chatId, usado también en los mensajes
    private String empresaId;
    private String candidatoId;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private String lastMessageContent; // Opcional: rápido acceso al último mensaje
    private Boolean isActive;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmpresaId() {
        return empresaId;
    }
    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }
    public String getCandidatoId() {
        return candidatoId;
    }
    public void setCandidatoId(String candidatoId) {
        this.candidatoId = candidatoId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getLastMessageAt() {
        return lastMessageAt;
    }
    public void setLastMessageAt(LocalDateTime lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }
    public String getLastMessageContent() {
        return lastMessageContent;
    }
    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    } 
}