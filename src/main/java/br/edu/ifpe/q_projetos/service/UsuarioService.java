package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.DTO.UsuarioCreateDTO;
import br.edu.ifpe.q_projetos.DTO.UsuarioResponseDTO;
import br.edu.ifpe.q_projetos.DTO.UsuarioUpdateDTO;
import br.edu.ifpe.q_projetos.model.Usuario;
import br.edu.ifpe.q_projetos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public UsuarioResponseDTO cadastrarUsuario(UsuarioCreateDTO dto) {

        // Verifica se o e-mail já existe
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Este e-mail já está cadastrado no sistema.");
        }

        Usuario usuario = new Usuario();

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());

        // Como este método agora é administrativo,
        // o ADMIN pode definir role e vínculo manualmente
        usuario.setRole(
                dto.getRole() != null
                        ? dto.getRole()
                        : Usuario.Role.ROLE_USER);

        usuario.setVinculo(dto.getVinculo());

        // Senha obrigatória para cadastro administrativo
        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new RuntimeException("A senha é obrigatória.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        return toResponseDTO(repository.save(usuario));
    }

    public List<UsuarioResponseDTO> listarTodos() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("Usuário não autenticado.");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Acesso negado: apenas administradores podem listar todos os usuários.");
        }

        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 1. Verifica se existe alguém autenticado e se não é o usuário anônimo
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("Acesso negado: você precisa estar logado para acessar este recurso.");
        }

        String emailLogado = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 2. Se for ADMIN, busca qualquer um
        if (isAdmin) {
            return repository.findById(id).map(this::toResponseDTO)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID solicitado."));
        }

        // 3. Se não for ADMIN, busca o dono da conta
        Usuario logado = repository.findByEmail(emailLogado)
                .orElseThrow(() -> new RuntimeException("Erro crítico: perfil do usuário logado não encontrado."));

        if (!logado.getId().equals(id)) {
            throw new RuntimeException("Acesso negado: você só pode visualizar o seu próprio perfil.");
        }

        return toResponseDTO(logado);
    }

    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioUpdateDTO dto) {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                auth.getName().equals("anonymousUser")) {

            throw new RuntimeException(
                    "Usuário não autenticado.");
        }

        String emailLogado = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Usuario usuario = repository.findById(id).orElseThrow(() -> new RuntimeException(
                "Usuário não encontrado para atualização."));

        boolean isDonoDaConta = usuario.getEmail().equals(emailLogado);

        // Apenas admin ou dono da conta
        if (!isAdmin && !isDonoDaConta) {
            throw new RuntimeException("Acesso negado: você não tem permissão para editar este perfil.");
        }

        // Atualiza nome
        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            usuario.setNome(dto.getNome());
        }

        // Apenas ADMIN altera role e vínculo
        if (isAdmin) {
            if (dto.getRole() != null) {
                usuario.setRole(dto.getRole());
            }

            if (dto.getVinculo() != null) {
                usuario.setVinculo(dto.getVinculo());
            }
        }

        // Atualização segura da senha
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        Usuario usuarioAtualizado = repository.save(usuario);

        return toResponseDTO(usuarioAtualizado);
    }

    public void deletarUsuario(Long id) { // Ajustado nome conforme especificação
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailLogado = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Usuario usuarioParaDeletar = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        boolean isDonoDaConta = usuarioParaDeletar.getEmail().equals(emailLogado);

        if (!isAdmin && !isDonoDaConta) {
            throw new RuntimeException("Acesso negado: você não tem permissão para excluir este perfil.");
        }

        repository.deleteById(id);
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