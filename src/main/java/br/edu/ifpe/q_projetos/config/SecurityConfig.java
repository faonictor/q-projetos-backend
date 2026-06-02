package br.edu.ifpe.q_projetos.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

// NOTA: Substitua pelos caminhos corretos dos seus filtros e handlers quando criá-los
import br.edu.ifpe.q_projetos.security.JwtFilter;
import br.edu.ifpe.q_projetos.security.OAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    // Construtor para injetar os componentes de suporte à segurança
    public SecurityConfig(JwtFilter jwtFilter, OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.jwtFilter = jwtFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuração de CORS
                .cors(Customizer.withDefaults())

                // 2. Desativação do CSRF (Padrão para APIs REST que utilizam tokens JWT)
                .csrf(csrf -> csrf.disable())

                // 3. Configura a sessão como STATELESS (Sem guardar estado no servidor)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Regras de Autorização de Rotas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/**", "/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/projetos/**").permitAll()
                        .requestMatchers("/api/usuarios/**").authenticated()
                        .anyRequest().authenticated()
                )

                // 5. MODIFICAÇÃO PRINCIPAL: Impede o redirecionamento para a página HTML do Google
                // Força a API a devolver status 401 (Unauthorized) quando faltar o token ou ele for inválido
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acesso Negado ou Token Inválido");
                        })
                )

                // 6. Configuração do Login Social com Google (OAuth2)
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization")
                        )
                        .successHandler(oAuth2SuccessHandler)
                );

        // 7. MODIFICAÇÃO PRINCIPAL: Descomentado!
        // O filtro agora está ativo e vai interceptar as requisições para ler e validar o token JWT
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permite o frontend rodando no Ionic/Angular/React em localhost:8100
        configuration.setAllowedOrigins(List.of("http://localhost:8100"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}