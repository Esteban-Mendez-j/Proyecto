package com.miproyecto.proyecto.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.miproyecto.proyecto.domain.Chat;

public interface ChatRepository extends MongoRepository<Chat, String> {

}
