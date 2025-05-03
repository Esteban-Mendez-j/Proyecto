package com.miproyecto.proyecto.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.miproyecto.proyecto.domain.Chat;

public interface ChatRepository extends MongoRepository<Chat, String> {
    Optional<Chat> findByEmpresaIdAndCandidatoId(String empresaId, String candidatoId);

    List<Chat> findByEmpresaId(String empresaId);

    List<Chat> findByCandidatoId(String candidatoId);
}
