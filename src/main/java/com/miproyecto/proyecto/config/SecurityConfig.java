package com.miproyecto.proyecto.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfigurationSource;

import com.miproyecto.proyecto.config.filter.JwtTokenValidator;
import com.miproyecto.proyecto.service.CustomUserDetailsService;
import com.miproyecto.proyecto.service.UsuarioService;
import com.miproyecto.proyecto.util.JwtUtils;

@Configuration
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SecurityConfig(JwtUtils jwtUtils, CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.jwtUtils = jwtUtils;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UsuarioService usuarioService){
        return new CustomUserDetailsService(usuarioService);
    }

    /*
     * Manejo de sessiones
     * mantiene a Spring Security actualizado sobre los eventos del ciclo de vida de la sesión
     */
    @Bean
    HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /*
     * Configura un `DaoAuthenticationProvider`, que es el componente encargado 
     * de autenticar a los usuarios a través de `UserDetailsService` y `PasswordEncoder`.
    */ 
    @Bean
    DaoAuthenticationProvider authenticationProvider(UserDetailsService
            userDetailsService, PasswordEncoder passwordEncoder) {
        
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /*
     * Crea y configura un `AuthenticationManager`, el componente central de 
     * Spring Security que gestiona la autenticación de los usuarios.
    */
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http,
                                            UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) throws Exception {
        
        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(authenticationProvider(userDetailsService,passwordEncoder))
            .build();
    }

    //luego del inicio de sesion,  redirecciona dependiendo del rol 
    @Bean
    AuthenticationSuccessHandler customSuccessHandler(JwtUtils jwtUtils) {
        return new CustomAuthenticationSuccessHandler(jwtUtils);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, 
            @Qualifier("customCorsConfig") CorsConfigurationSource corsConfig) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfig))       
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/usuarios/**", "/css/**", 
                    "/images/**", "/js/**", "/api/empresas/add", "/api/candidatos/add","/api/usuarios/rol",
                    "/api/vacantes/listar", "/api/vacantes/seleccion/{nvacantes}",
                    "/api/vacantes/eliminar/filtro", "/api/apelaciones/**","/api/vacantes/Top/listar","/api/vacantes/listar/filtradas" ,
                    "/api/vacantes/**", "/api/empresas/perfil/**"
                ).permitAll()
                .requestMatchers("/api/admin/agregarRol","/admin/removerRol").hasRole("SUPER_ADMIN")
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/empresas/**","/api/vacantes/popular/listar").hasRole("EMPRESA")
                .requestMatchers("/api/candidatos/perfil/**","/api/postulados/**").hasAnyRole("CANDIDATO","EMPRESA","ADMIN","SUPER_ADMIN")
                .requestMatchers("/api/candidatos/**", "/api/estudios/**", "/api/historialLaborals/**").hasRole("CANDIDATO")
                .requestMatchers("/api/chats/**", "/app/chat.sendMessage").hasAnyRole("EMPRESA", "CANDIDATO")
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin                       
				.loginPage("http://localhost:4321/login")
                .loginProcessingUrl("/api/usuarios/login")
                .failureHandler(customAuthenticationFailureHandler)
                .successHandler(customSuccessHandler(jwtUtils))
				.permitAll()
			)
			.logout(logout -> logout                                   
				.logoutUrl("/usuarios/cerrarSesion")
				.logoutSuccessUrl("http://localhost:4321/?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "jwtToken")
				.permitAll()
            )
            .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class);
        return http.build();
    }

    public JwtUtils getJwtUtils() {
        return jwtUtils;
    }

    public CustomAuthenticationFailureHandler getCustomAuthenticationFailureHandler() {
        return customAuthenticationFailureHandler;
    }
}
