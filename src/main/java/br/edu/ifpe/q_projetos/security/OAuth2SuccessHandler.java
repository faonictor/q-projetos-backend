package br.edu.ifpe.q_projetos.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import br.edu.ifpe.q_projetos.model.Usuario;
import br.edu.ifpe.q_projetos.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. Extrai os dados do usuário vindos do Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String nome = oAuth2User.getAttribute("name");

        // 2. Busca o usuário no banco ou cria um novo se não existir
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> {
                    Usuario novoUsuario = Usuario.builder()
                            .nome(nome)
                            .email(email)
                            .role(Usuario.Role.ROLE_USER) // Estudantes entram como ROLE_USER
                            .vinculo(Usuario.Vinculo.ESTUDANTE)
                            .build();
                    return usuarioRepository.save(novoUsuario);
                });

        // 3. Gera o Token JWT da nossa API para este usuário
        String token = jwtService.generateToken(usuario);

        // 4. Redireciona para o Front-end (Ionic/Angular) passando o token na URL
        // Em produção, essa URL virá de uma variável de ambiente
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8100/login-success")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}