package br.edu.ifpe.q_projetos.repository;

import br.edu.ifpe.q_projetos.model.VinculoEquipe;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VinculoEquipeRepository
        extends JpaRepository<VinculoEquipe, Long> {

    Optional<VinculoEquipe> findByIdProjetoAndIdUsuario(Long idProjeto, Long idUsuario);
}
