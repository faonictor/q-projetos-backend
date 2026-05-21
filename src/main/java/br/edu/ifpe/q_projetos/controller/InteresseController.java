package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.model.Interesse;
import br.edu.ifpe.q_projetos.service.InteresseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interesses")
public class InteresseController {

    @Autowired
    private InteresseService service;

    @GetMapping
    public List<Interesse> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Interesse buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Interesse salvar(@RequestBody Interesse interesse) {
        return service.salvar(interesse);
    }

    @PutMapping("/{id}")
    public Interesse atualizar(
            @PathVariable Long id,
            @RequestBody Interesse interesse) {

        return service.atualizar(id, interesse);
    }

    @GetMapping("/projeto/{projetoId}")
    public ResponseEntity<List<Interesse>> listarLeadsPorProjeto(
            @PathVariable("projetoId") Long projetoId) {

        List<Interesse> leads = service.listarLeadsPorProjeto(projetoId);

        return ResponseEntity.ok(leads);
    }
    
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
