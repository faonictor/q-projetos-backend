package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.model.VinculoEquipe;
import br.edu.ifpe.q_projetos.service.VinculoEquipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vinculos")
public class VinculoEquipeController {

    private final VinculoEquipeService service;

    public VinculoEquipeController(
            VinculoEquipeService service
    ) {
        this.service = service;
    }

    @PostMapping
    public VinculoEquipe criar(
            @RequestBody VinculoEquipe vinculo,
            @RequestParam("usuarioLogado") Long usuarioLogado
    ) {

        return service.salvar(
                vinculo,
                usuarioLogado
        );
    }

    @GetMapping
    public List<VinculoEquipe> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public VinculoEquipe buscar(
            @PathVariable Long id
    ) {

        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public VinculoEquipe atualizar(
            @PathVariable Long id,
            @RequestBody VinculoEquipe vinculo,
            @RequestParam("usuarioLogado") Long usuarioLogado
    ) {

        return service.atualizar(
                id,
                vinculo,
                usuarioLogado
        );
    }

    @DeleteMapping("/{id}")
    public void deletar(
            @PathVariable Long id,
            @RequestParam("usuarioLogado") Long usuarioLogado
    ) {

        service.deletar(
                id,
                usuarioLogado
        );
    }
}