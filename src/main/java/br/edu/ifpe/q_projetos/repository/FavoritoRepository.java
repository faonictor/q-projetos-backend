package br.edu.ifpe.q_projetos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifpe.q_projetos.model.Favorito;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    Optional<Favorito> findByIdUsuarioAndIdProjeto(Long idUsuario, Long idProjeto);
    List<Favorito> findByIdUsuario(Long idUsuario); // histórico do estudante
    void deleteByIdProjeto(Long idProjeto);
}
