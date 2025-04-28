package com.miproyecto.proyecto.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mensajes")
public class Mensaje {

    @Id
    private String id;
    private String chatId;
    private String senderId;
    private String receiverId;
    private String senderRole;  
    private String receiverRole; 
    private String content;
    private LocalDateTime timestamp;
    private String state;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    public String getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    public String getSenderRole() {
        return senderRole;
    }
    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }
    public String getReceiverRole() {
        return receiverRole;
    }
    public void setReceiverRole(String receiverRole) {
        this.receiverRole = receiverRole;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
}