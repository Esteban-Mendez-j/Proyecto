package com.miproyecto.proyecto.repos;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.miproyecto.proyecto.domain.Mensaje;

public interface MensajeRepository extends MongoRepository<Mensaje, String> {
    List<Mensaje> findByChatIdOrderByTimeAsc(String chatId);
}
