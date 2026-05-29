package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.DTO.UsuarioCreateDTO;
import br.edu.ifpe.q_projetos.DTO.UsuarioPerfilUpdateDTO;
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
        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Regra de Negócio: Este e-mail já está cadastrado no sistema.");
        }

        // Validação de Role vs Vínculo
        if (Usuario.Role.ROLE_COORD.equals(dto.getRole()) && !Usuario.Vinculo.SERVIDOR.equals(dto.getVinculo())) {
            throw new RuntimeException("Regra de Negócio: Apenas usuários com vínculo SERVIDOR podem ser Coordenadores.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setRole(dto.getRole() != null ? dto.getRole() : Usuario.Role.ROLE_USER);
        usuario.setVinculo(dto.getVinculo());

        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new RuntimeException("Regra de Negócio: A senha é obrigatória.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        return toResponseDTO(repository.save(usuario));
    }

    public List<UsuarioResponseDTO> listarTodos() {
        validarPermissaoAdmin();
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
        
        validarPermissaoDonoOuAdmin(usuario.getEmail());
        return toResponseDTO(usuario);
    }

    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para atualização."));

        validarPermissaoDonoOuAdmin(usuario.getEmail());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            usuario.setNome(dto.getNome());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!dto.getEmail().equals(usuario.getEmail()) && repository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Regra de Negócio: Este e-mail já está em uso por outro usuário.");
            }
            usuario.setEmail(dto.getEmail());
        }

        if (isAdmin) {
            // Validação de Role vs Vínculo para Admin
            Usuario.Role novaRole = dto.getRole() != null ? dto.getRole() : usuario.getRole();
            Usuario.Vinculo novoVinculo = dto.getVinculo() != null ? dto.getVinculo() : usuario.getVinculo();

            if (Usuario.Role.ROLE_COORD.equals(novaRole) && !Usuario.Vinculo.SERVIDOR.equals(novoVinculo)) {
                throw new RuntimeException("Regra de Negócio: Apenas usuários com vínculo SERVIDOR podem ser Coordenadores.");
            }

            if (dto.getRole() != null) usuario.setRole(dto.getRole());
            if (dto.getVinculo() != null) usuario.setVinculo(dto.getVinculo());
        }

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        return toResponseDTO(repository.save(usuario));
    }

    public UsuarioResponseDTO atualizarPerfil(UsuarioPerfilUpdateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("Acesso negado: Usuário não autenticado.");
        }

        Usuario usuario = repository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            usuario.setNome(dto.getNome());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            // Se o e-mail mudou, verifica se já existe
            if (!dto.getEmail().equals(usuario.getEmail()) && repository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Regra de Negócio: Este e-mail já está em uso por outro usuário.");
            }
            usuario.setEmail(dto.getEmail());
        }

        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        return toResponseDTO(repository.save(usuario));
    }

    public void deletarUsuario(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        validarPermissaoDonoOuAdmin(usuario.getEmail());
        repository.deleteById(id);
    }

    private void validarPermissaoAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new RuntimeException("Acesso negado: Ação exclusiva para administradores.");
        }
    }

    private void validarPermissaoDonoOuAdmin(String emailDono) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("Acesso negado: Usuário não autenticado.");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isDono = auth.getName().equals(emailDono);

        if (!isAdmin && !isDono) {
            throw new RuntimeException("Acesso negado: Você não possui permissão para acessar estes dados.");
        }
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
