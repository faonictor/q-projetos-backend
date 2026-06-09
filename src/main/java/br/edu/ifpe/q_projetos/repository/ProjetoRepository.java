package br.edu.ifpe.q_projetos.repository;

import br.edu.ifpe.q_projetos.model.Projeto;
import br.edu.ifpe.q_projetos.model.Projeto.TipoProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    // Busca por título ou descrição (Case Insensitive)
    List<Projeto> findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(
            String titulo,
            String descricao
    );

    // Busca simples por tipo
    List<Projeto> findByTipo(TipoProjeto tipo);

    // --- BUSCAS BASEADAS NO STATUS DINÂMICO (DATAS) ---

    // Projetos com inscrições ABERTAS (Data atual entre início e fim)
    @Query("SELECT p FROM Projeto p WHERE :hoje BETWEEN p.dataInicioInscricao AND p.dataFimInscricao")
    List<Projeto> findProjetosComInscricoesAbertas(@Param("hoje") LocalDate hoje);

    // Projetos com inscrições ENCERRADAS (Data atual após o fim)
    @Query("SELECT p FROM Projeto p WHERE :hoje > p.dataFimInscricao")
    List<Projeto> findProjetosComInscricoesEncerradas(@Param("hoje") LocalDate hoje);

    // Projetos AGUARDANDO início (Data atual antes do início)
    @Query("SELECT p FROM Projeto p WHERE :hoje < p.dataInicioInscricao")
    List<Projeto> findProjetosAguardandoInscricao(@Param("hoje") LocalDate hoje);

    // Filtro combinado: Tipo + Inscrições Abertas
    @Query("SELECT p FROM Projeto p WHERE p.tipo = :tipo AND :hoje BETWEEN p.dataInicioInscricao AND p.dataFimInscricao")
    List<Projeto> findByTipoAndInscricoesAbertas(
            @Param("tipo") TipoProjeto tipo, 
            @Param("hoje") LocalDate hoje
    );
    List<Projeto> findByIdIn(List<Long> ids);
}