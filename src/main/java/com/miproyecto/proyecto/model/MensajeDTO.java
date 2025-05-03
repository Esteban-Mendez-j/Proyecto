package com.miproyecto.proyecto.model;

import java.time.LocalDateTime;

public class MensajeDTO {
    private String chatId;
    private String senderId;
    private String receiverId;
    private String senderRole;
    private String receiverRole;
    private String content;
    private LocalDateTime time;
    private String state;

    public MensajeDTO() {
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
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
    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}