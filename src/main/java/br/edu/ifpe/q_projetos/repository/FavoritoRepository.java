package br.edu.ifpe.q_projetos.repository;

import br.edu.ifpe.q_projetos.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    Optional<Favorito> findByIdUsuarioAndIdProjeto(Long idUsuario, Long idProjeto);
    List<Favorito> findByIdUsuario(Long idUsuario); // histórico do estudante
}
