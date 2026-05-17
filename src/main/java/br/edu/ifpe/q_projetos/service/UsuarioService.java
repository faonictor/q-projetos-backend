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

    @Autowired // Corrigido: Injeção via Spring de acordo com o plano de arquitetura
    private BCryptPasswordEncoder passwordEncoder;

    public UsuarioResponseDTO cadastrarUsuario(UsuarioCreateDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Regra de Negócio: Este e-mail já está cadastrado no sistema.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());

        // --- PROTEÇÃO DE GOVERNANÇA E VÍNCULO ---
        if (!isAdmin) {
            // Se NÃO for admin, o sistema ignora o que veio no DTO e decide pelo e-mail
            usuario.setRole(Usuario.Role.ROLE_USER);

            if (dto.getEmail().endsWith("@discente.ifpe.edu.br")) {
                usuario.setVinculo(Usuario.Vinculo.ESTUDANTE);
            } else if (dto.getEmail().endsWith("@ifpe.edu.br") || dto.getEmail().endsWith(".ifpe.edu.br")) {
                usuario.setVinculo(Usuario.Vinculo.SERVIDOR);
            } else {
                // Impede que e-mails pessoais (gmail, hotmail) se cadastrem sem validação
                throw new RuntimeException("Regra de Negócio: É necessário utilizar um e-mail institucional do IFPE.");
            }
        } else {
            // Se for ADMIN logado, ele tem poder total para definir qualquer role ou
            // vínculo
            usuario.setRole(dto.getRole() != null ? dto.getRole() : Usuario.Role.ROLE_USER);
            usuario.setVinculo(dto.getVinculo());
        }

        // Criptografia de senha
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        return toResponseDTO(repository.save(usuario));
    }

    // public UsuarioResponseDTO cadastrarUsuario(UsuarioCreateDTO dto) {
    //     // 1. Validação de e-mail único (Regra de Negócio Obrigatória)
    //     if (repository.existsByEmail(dto.getEmail())) {
    //         throw new RuntimeException("Regra de Negócio: Este e-mail já está cadastrado no sistema.");
    //     }

    //     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     boolean isAdmin = auth != null && auth.getAuthorities().stream()
    //             .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    //     Usuario usuario = new Usuario();
    //     usuario.setNome(dto.getNome());
    //     usuario.setEmail(dto.getEmail());
    //     usuario.setVinculo(dto.getVinculo());

    //     // 2. Proteção contra Escala de Privilégios (RF17 e RF18)
    //     if (!isAdmin) {
    //         usuario.setRole(Usuario.Role.ROLE_USER);
    //     } else {
    //         usuario.setRole(dto.getRole());
    //     }

    //     // 3. Segurança da Senha adaptada para Google OAuth2 (Prevenção de Bug)
    //     if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
    //         String senhaComHash = passwordEncoder.encode(dto.getSenha());
    //         usuario.setSenha(senhaComHash);
    //     } else {
    //         usuario.setSenha(null); // Permite senha nula para logins puramente sociais
    //     }

    //     return toResponseDTO(repository.save(usuario));
    // }

    public List<UsuarioResponseDTO> listarTodos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new RuntimeException("Acesso negado: apenas administradores podem listar todos os usuários.");
        }

        return repository.findAll().stream()
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

    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioUpdateDTO dto) { // Ajustado nome conforme especificação
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailLogado = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return repository.findById(id).map(usuario -> {
            boolean isDonoDaConta = usuario.getEmail().equals(emailLogado);

            if (!isAdmin && !isDonoDaConta) {
                throw new RuntimeException("Acesso negado: você não tem permissão para editar este perfil.");
            }

            if (dto.getNome() != null)
                usuario.setNome(dto.getNome());
            if (dto.getEmail() != null)
                usuario.setEmail(dto.getEmail());

            // Apenas ADMIN altera Role e Vínculo (RF17)
            if (isAdmin) {
                if (dto.getRole() != null)
                    usuario.setRole(dto.getRole());
                if (dto.getVinculo() != null)
                    usuario.setVinculo(dto.getVinculo());
            }

            // Atualização de senha segura
            if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
                String senhaComHash = passwordEncoder.encode(dto.getSenha());
                usuario.setSenha(senhaComHash);
            }

            return toResponseDTO(repository.save(usuario));
        }).orElseThrow(() -> new RuntimeException("Usuário não encontrado para atualização."));
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