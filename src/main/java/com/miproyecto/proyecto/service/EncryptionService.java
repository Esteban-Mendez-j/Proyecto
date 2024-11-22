package com.miproyecto.proyecto.service;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;


@Service
public class EncryptionService {

    private TextEncryptor encryptor;

    public EncryptionService() {
        String password = "mySecretPassword"; // Clave segura
        String salt = "1234567890abcdef"; // Sal hexadecimal válida
        this.encryptor = Encryptors.text(password, salt); // Configuración de encriptador
    }

    public String encrypt(Long idDataBase) {
        return encryptor.encrypt(String.valueOf(idDataBase));
    }

    public Long decrypt(String encryptedText) {
        return Long.valueOf(encryptor.decrypt(encryptedText));
    }
}
