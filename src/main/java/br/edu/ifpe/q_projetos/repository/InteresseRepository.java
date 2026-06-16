package br.edu.ifpe.q_projetos.repository;

import br.edu.ifpe.q_projetos.model.Interesse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteresseRepository extends JpaRepository<Interesse, Long> {

    List<Interesse> findByProjetoId(Long projetoId);

    boolean existsByEmailAndProjetoId(String email, Long projetoId);

    List<Interesse> findByProjetoIdIn(List<Long> projetosIds);
}
