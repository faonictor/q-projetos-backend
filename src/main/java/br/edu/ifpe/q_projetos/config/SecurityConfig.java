package br.edu.ifpe.q_projetos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Classe responsável por ditar as regras do "Segurança" (Spring Security).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desativa a proteção CSRF temporariamente.
                // O CSRF bloqueia requisições POST, PUT e DELETE que vêm de fora do navegador (como o Postman).
                .csrf(csrf -> csrf.disable())

                // 2. Regras de Autorização
                .authorizeHttpRequests(auth -> auth
                        // Para já, vamos dizer ao segurança: "Deixa toda a gente passar para qualquer rota"
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}