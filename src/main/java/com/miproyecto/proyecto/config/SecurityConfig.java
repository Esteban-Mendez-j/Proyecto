package com.miproyecto.proyecto.config;

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

import com.miproyecto.proyecto.config.filter.JwtTokenValidator;
import com.miproyecto.proyecto.repos.UsuarioRepository;
import com.miproyecto.proyecto.service.CustomUserDetailsService;
import com.miproyecto.proyecto.util.JwtUtils;

@Configuration
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UsuarioRepository usuarioRepository){
        return new CustomUserDetailsService(usuarioRepository);
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
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/usuarios/**", "/css/**", 
                    "/images/**", "/js/**", "/empresas/add", "candidatos/add", 
                    "/vacantes/listar", "/vacantes/seleccion/{nvacantes}",
                    "/vacantes/eliminar/filtro" 
                ).permitAll()
                .requestMatchers("/empresas/**").hasRole("EMPRESA")
                .requestMatchers("/candidatos/**").hasRole("CANDIDATO")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/vacantes/**", "/postulados/**", 
                    "/estudios", "/historialLaborals").hasAnyRole("EMPRESA", "CANDIDATO")
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin                          
				.loginPage("/usuarios/login")
				.loginProcessingUrl("/usuarios/login")
                .successHandler(customSuccessHandler(jwtUtils))
                .failureUrl("/usuarios/login/error")
				.permitAll()
			)
			.logout(logout -> logout                                   
				.logoutUrl("/usuarios/cerrarSesion")
				.logoutSuccessUrl("/?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
				.permitAll()
            )
            .addFilterBefore(new JwtTokenValidator(jwtUtils), BasicAuthenticationFilter.class);
        return http.build();
    }
}
