package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.dto.FavoritoDTO;
import br.edu.ifpe.q_projetos.dto.FavoritoResponseDTO;
import br.edu.ifpe.q_projetos.service.FavoritoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FavoritoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/meu-historico")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FavoritoResponseDTO>> listarMeuHistorico() {
        return ResponseEntity.ok(service.listarMeuHistorico());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoritoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoritoResponseDTO> salvar(@Valid @RequestBody FavoritoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.vincularFavorito(dto));
    }

    @DeleteMapping("/projeto/{idProjeto}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void desvincular(@PathVariable Long idProjeto) {
        service.desvincularFavorito(idProjeto);
    }
}
