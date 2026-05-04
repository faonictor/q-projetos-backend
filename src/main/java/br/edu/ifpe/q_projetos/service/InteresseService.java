package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.model.Interesse;
import br.edu.ifpe.q_projetos.repository.InteresseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InteresseService {

    @Autowired
    private InteresseRepository repository;

    public Interesse salvar(Interesse interesse) {
        return repository.save(interesse);
    }

    public List<Interesse> listarTodos() {
        return repository.findAll();
    }
}
