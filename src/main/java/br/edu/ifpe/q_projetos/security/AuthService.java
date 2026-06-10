package br.edu.ifpe.q_projetos.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpe.q_projetos.dto.UsuarioCreateDTO;
import br.edu.ifpe.q_projetos.dto.UsuarioResponseDTO;
import br.edu.ifpe.q_projetos.model.Usuario;
import br.edu.ifpe.q_projetos.repository.UsuarioRepository;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));
    }

    public UsuarioResponseDTO registrar(UsuarioCreateDTO dto) {

        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException(
                    "Este e-mail já está cadastrado.");
        }

        Usuario usuario = new Usuario();

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());

        usuario.setRole(Usuario.Role.ROLE_USER);

        if (dto.getEmail().endsWith("@discente.ifpe.edu.br")) {
            usuario.setVinculo(Usuario.Vinculo.ESTUDANTE);
        } else if (dto.getEmail().endsWith("@ifpe.edu.br") || dto.getEmail().endsWith(".ifpe.edu.br")) {
            usuario.setVinculo(Usuario.Vinculo.SERVIDOR);
        } else {
            throw new RuntimeException("Utilize um e-mail institucional do IFPE.");
        }

        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new RuntimeException("A senha é obrigatória.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return toResponseDTO(usuarioSalvo);
    }

    @Autowired(required = false)
    private org.springframework.mail.javamail.JavaMailSender mailSender;

    public void solicitarRecuperacaoSenha(String email, jakarta.servlet.http.HttpServletRequest request) {
        if (email == null || email.isBlank()) {
            throw new br.edu.ifpe.q_projetos.exception.RegraNegocioException("O e-mail é obrigatório.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new br.edu.ifpe.q_projetos.exception.RegraNegocioException(
                        "Este e-mail não está cadastrado no sistema."));

        String token = java.util.UUID.randomUUID().toString();
        usuario.setTokenRecuperacao(token);
        usuario.setExpiracaoTokenRecuperacao(java.time.LocalDateTime.now().plusMinutes(10));
        usuarioRepository.save(usuario);

        String origin = request.getHeader("Origin");
        if (origin == null || origin.isBlank()) {
            origin = "https://q-projetos.onrender.com";
        }
        String linkRedefinicao = origin + "/redefinir-senha?token=" + token;

        String serverName = request.getServerName();
        boolean isLocal = "localhost".equalsIgnoreCase(serverName) || 
                          "127.0.0.1".equals(serverName) || 
                          "0:0:0:0:0:0:0:1".equals(serverName);

        if (isLocal) {
            System.out.println("==================================================");
            System.out.println("SIMULAÇÃO DE ENVIO DE E-MAIL (LOCALHOST)");
            System.out.println("Para: " + email);
            System.out.println("Link de Redefinição: " + linkRedefinicao);
            System.out.println("==================================================");
        } else {
            if (mailSender == null) {
                throw new br.edu.ifpe.q_projetos.exception.RegraNegocioException(
                        "Serviço de e-mail não configurado no servidor.");
            }
            try {
                org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
                message.setFrom("no-reply@q-projetos.ifpe.edu.br");
                message.setTo(email);
                message.setSubject("Recuperação de Senha - Q-Projetos");
                message.setText("Olá, " + usuario.getNome() + "!\n\n" +
                        "Você solicitou a recuperação de sua senha na Plataforma Q-Projetos.\n" +
                        "Clique no link abaixo para redefinir sua senha. Este link é válido por 10 minutos:\n\n" +
                        linkRedefinicao + "\n\n" +
                        "Se você não solicitou esta redefinição, desconsidere este e-mail.");
                mailSender.send(message);
            } catch (Exception e) {
                throw new br.edu.ifpe.q_projetos.exception.RegraNegocioException(
                        "Erro ao enviar o e-mail de recuperação: " + e.getMessage());
            }
        }
    }

    public boolean validarTokenRecuperacao(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Usuario usuario = usuarioRepository.findByTokenRecuperacao(token).orElse(null);
        if (usuario == null) {
            return false;
        }
        if (usuario.getExpiracaoTokenRecuperacao() == null || 
            usuario.getExpiracaoTokenRecuperacao().isBefore(java.time.LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    public void redefinirSenha(String token, String novaSenha) {
        if (token == null || token.isBlank()) {
            throw new br.edu.ifpe.q_projetos.exception.RegraNegocioException("Token de recuperação inválido.");
        }
        if (novaSenha == null || novaSenha.isBlank() || novaSenha.length() < 6) {
            throw new br.edu.ifpe.q_projetos.exception.RegraNegocioException("A senha deve ter no mínimo 6 caracteres.");
        }

        Usuario usuario = usuarioRepository.findByTokenRecuperacao(token)
                .orElseThrow(() -> new br.edu.ifpe.q_projetos.exception.RegraNegocioException("Token de recuperação inválido ou não encontrado."));

        if (usuario.getExpiracaoTokenRecuperacao() == null || 
            usuario.getExpiracaoTokenRecuperacao().isBefore(java.time.LocalDateTime.now())) {
            throw new br.edu.ifpe.q_projetos.exception.RegraNegocioException("O token expirou. Solicite a recuperação novamente.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setTokenRecuperacao(null);
        usuario.setExpiracaoTokenRecuperacao(null);
        usuarioRepository.save(usuario);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setEmail(usuario.getEmail());
        response.setRole(usuario.getRole());
        response.setVinculo(usuario.getVinculo());
        return response;
    }
}
