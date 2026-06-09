package br.edu.ifpe.q_projetos.repository;

import br.edu.ifpe.q_projetos.model.VinculoEquipe;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VinculoEquipeRepository
        extends JpaRepository<VinculoEquipe, Long> {

    Optional<VinculoEquipe> findByIdProjetoAndIdUsuario(Long idProjeto, Long idUsuario);
    List<VinculoEquipe> findByIdUsuarioAndPapelAndAtivo(
        Long idUsuario, VinculoEquipe.Papel papel, Boolean ativo);
    
}
