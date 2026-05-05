
package br.edu.ifpe.q_projetos.service;


import br.edu.ifpe.q_projetos.model.VinculoEquipe;
import br.edu.ifpe.q_projetos.repository.VinculoEquipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VinculoEquipeService {

    private final VinculoEquipeRepository repository;

    public VinculoEquipeService(VinculoEquipeRepository repository) {
        this.repository = repository;
    }

    public VinculoEquipe salvar(VinculoEquipe vinculo) {
        return repository.save(vinculo);
    }

    public List<VinculoEquipe> listarTodos() {
        return repository.findAll();
    }

    public Optional<VinculoEquipe> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }
}