package br.edu.ifpe.q_projetos.repository;
import br.edu.ifpe.q_projetos.model.Projeto;
import br.edu.ifpe.q_projetos.model.Projeto.StatusInscricao;
import br.edu.ifpe.q_projetos.model.Projeto.TipoProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    List<Projeto> findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
            String titulo,
            String descricao
    );

    List<Projeto> findByTipo(TipoProjeto tipo);
    List<Projeto> findByStatusInscricao(StatusInscricao statusInscricao);
    List<Projeto> findByTipoAndStatusInscricao(
            TipoProjeto tipo,
            StatusInscricao statusInscricao
    );
}