package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.model.Favorito;
import br.edu.ifpe.q_projetos.repository.FavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoRepository favoritoRepository;

    // GET - todos
    @GetMapping
    public List<Favorito> listarTodos() {
        return favoritoRepository.findAll();
    }

    // GET - por ID
    @GetMapping("/{id}")
    public ResponseEntity<Favorito> buscarPorId(@PathVariable Long id) {
        return favoritoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST - criar favorito (dataRegistro é automático)
    @PostMapping
    public Favorito salvar(@RequestBody Favorito favorito) {
        return favoritoRepository.save(favorito);
    }

    // DELETE - remover favorito por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return favoritoRepository.findById(id)
                .map(f -> {
                    favoritoRepository.delete(f);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}