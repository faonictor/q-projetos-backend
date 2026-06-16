package br.edu.ifpe.q_projetos.repository;

import br.edu.ifpe.q_projetos.model.Interesse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteresseRepository extends JpaRepository<Interesse, Long> {

    // 🔎 já existia (mantém)
    List<Interesse> findByProjetoId(Long projetoId);

    // 🔒 valida unicidade (email + projeto)
    boolean existsByEmailAndProjetoId(String email, Long projetoId);

    // 🔐 usado para filtrar projetos do coordenador
    List<Interesse> findByProjetoIdIn(List<Long> projetosIds);
}
