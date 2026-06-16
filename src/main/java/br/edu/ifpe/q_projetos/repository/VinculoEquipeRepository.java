package br.edu.ifpe.q_projetos.repository;

import br.edu.ifpe.q_projetos.model.VinculoEquipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VinculoEquipeRepository
        extends JpaRepository<VinculoEquipe, Long> {

    Optional<VinculoEquipe> findByIdProjetoAndIdUsuario(Long idProjeto, Long idUsuario);

    List<VinculoEquipe> findByIdProjeto(Long idProjeto);

    boolean existsByIdProjetoAndIdUsuarioAndAtivoTrue(Long idProjeto, Long idUsuario);
}
