package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.dto.UsuarioCreateDTO;
import br.edu.ifpe.q_projetos.dto.UsuarioPerfilUpdateDTO;
import br.edu.ifpe.q_projetos.dto.UsuarioResponseDTO;
import br.edu.ifpe.q_projetos.dto.UsuarioUpdateDTO;
import br.edu.ifpe.q_projetos.exception.RegraNegocioException;
import br.edu.ifpe.q_projetos.exception.RecursoNaoEncontradoException;
import br.edu.ifpe.q_projetos.model.Usuario;
import br.edu.ifpe.q_projetos.repository.UsuarioRepository;
import br.edu.ifpe.q_projetos.security.SecurityUtils;
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
            throw new RegraNegocioException("Regra de Negócio: Este e-mail já está cadastrado no sistema.");
        }

        // Validação de Role vs Vínculo
        if (Usuario.Role.ROLE_COORD.equals(dto.getRole()) && !Usuario.Vinculo.SERVIDOR.equals(dto.getVinculo())) {
            throw new RegraNegocioException("Regra de Negócio: Apenas usuários com vínculo SERVIDOR podem ser Coordenadores.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setRole(dto.getRole() != null ? dto.getRole() : Usuario.Role.ROLE_USER);
        usuario.setVinculo(dto.getVinculo());

        if (dto.getSenha() == null || dto.getSenha().isBlank()) {
            throw new RegraNegocioException("Regra de Negócio: A senha é obrigatória.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        return toResponseDTO(repository.save(usuario));
    }

    public List<UsuarioResponseDTO> listarTodos() {
        SecurityUtils.validarPermissaoAdmin();
        return repository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com o ID: " + id));

        SecurityUtils.validarPermissaoDonoOuAdmin(usuario.getEmail());
        return toResponseDTO(usuario);
    }

    public UsuarioResponseDTO atualizarUsuario(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado para atualização."));

        SecurityUtils.validarPermissaoDonoOuAdmin(usuario.getEmail());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            usuario.setNome(dto.getNome());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!dto.getEmail().equals(usuario.getEmail()) && repository.existsByEmail(dto.getEmail())) {
                throw new RegraNegocioException("Regra de Negócio: Este e-mail já está em uso por outro usuário.");
            }
            usuario.setEmail(dto.getEmail());
        }

        if (isAdmin) {
            // Validação de Role vs Vínculo para Admin
            Usuario.Role novaRole = dto.getRole() != null ? dto.getRole() : usuario.getRole();
            Usuario.Vinculo novoVinculo = dto.getVinculo() != null ? dto.getVinculo() : usuario.getVinculo();

            if (Usuario.Role.ROLE_COORD.equals(novaRole) && !Usuario.Vinculo.SERVIDOR.equals(novoVinculo)) {
                throw new RegraNegocioException("Regra de Negócio: Apenas usuários com vínculo SERVIDOR podem ser Coordenadores.");
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
        Usuario usuario = SecurityUtils.getLoggedUser(repository);

        if (dto.getNome() != null && !dto.getNome().isBlank()) {
            usuario.setNome(dto.getNome());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            // Se o e-mail mudou, verifica se já existe
            if (!dto.getEmail().equals(usuario.getEmail()) && repository.existsByEmail(dto.getEmail())) {
                throw new RegraNegocioException("Regra de Negócio: Este e-mail já está em uso por outro usuário.");
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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        SecurityUtils.validarPermissaoDonoOuAdmin(usuario.getEmail());
        repository.deleteById(id);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .vinculo(usuario.getVinculo())
                .build();
    }
}
