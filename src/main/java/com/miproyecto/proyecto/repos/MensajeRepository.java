package com.miproyecto.proyecto.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.miproyecto.proyecto.domain.Mensaje;

public interface MensajeRepository extends MongoRepository<Mensaje, String> {

}
