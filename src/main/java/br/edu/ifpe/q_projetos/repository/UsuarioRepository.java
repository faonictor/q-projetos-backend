package br.edu.ifpe.q_projetos.repository;

import br.edu.ifpe.q_projetos.model.Favorito;
import br.edu.ifpe.q_projetos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Perfeito para o fluxo de autenticação/login e recuperação de perfil
    Optional<Usuario> findByEmail(String email);

    // Otimizado para validação de novos cadastros (Evita carregar o objeto inteiro na memória)
    boolean existsByEmail(String email);

    Optional<Favorito> findByUsername(String username);
}