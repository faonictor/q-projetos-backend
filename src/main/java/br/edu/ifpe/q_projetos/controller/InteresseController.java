package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.model.Interesse;
import br.edu.ifpe.q_projetos.service.InteresseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interesses")
public class InteresseController {

    @Autowired
    private InteresseService service;

    @PostMapping
    public Interesse salvar(@RequestBody Interesse interesse) {
        return service.salvar(interesse);
    }

    @GetMapping
    public List<Interesse> listarTodos() {
        return service.listarTodos();
    }
}
