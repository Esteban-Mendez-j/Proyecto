package com.miproyecto.proyecto.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("com.miproyecto.proyecto.domain")
@EnableJpaRepositories("com.miproyecto.proyecto.repos")
@EnableTransactionManagement
public class DomainConfig {
}
