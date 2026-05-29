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
