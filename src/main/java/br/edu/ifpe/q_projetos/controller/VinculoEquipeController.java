package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.model.VinculoEquipe;
import br.edu.ifpe.q_projetos.service.VinculoEquipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vinculos")
public class VinculoEquipeController {

    private final VinculoEquipeService service;

    public VinculoEquipeController(VinculoEquipeService service) {
        this.service = service;
    }

    @PostMapping
    public VinculoEquipe criar(@RequestBody VinculoEquipe vinculo) {
        return service.salvar(vinculo);
    }

    @GetMapping
    public List<VinculoEquipe> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Optional<VinculoEquipe> buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}