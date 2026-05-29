package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.dto.VinculoEquipeDTO;
import br.edu.ifpe.q_projetos.dto.VinculoEquipeResponseDTO;
import br.edu.ifpe.q_projetos.service.VinculoEquipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vinculos")
public class VinculoEquipeController {

    @Autowired
    private VinculoEquipeService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VinculoEquipeResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public ResponseEntity<VinculoEquipeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/projeto/{projetoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public ResponseEntity<List<VinculoEquipeResponseDTO>> listarPorProjeto(@PathVariable Long projetoId) {
        return ResponseEntity.ok(service.listarPorProjeto(projetoId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public ResponseEntity<VinculoEquipeResponseDTO> salvar(@Valid @RequestBody VinculoEquipeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public ResponseEntity<VinculoEquipeResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody VinculoEquipeDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
