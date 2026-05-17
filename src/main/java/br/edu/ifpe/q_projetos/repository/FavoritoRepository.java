package br.edu.ifpe.q_projetos.repository;

import br.edu.ifpe.q_projetos.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    Optional<Favorito> findByIdUsuarioAndIdProjeto(Long idUsuario, Long idProjeto);
    List<Favorito> findByIdUsuario(Long idUsuario); // histórico do estudante
}
