package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.dto.VinculoEquipeDTO;
import br.edu.ifpe.q_projetos.dto.VinculoEquipeResponseDTO;
import br.edu.ifpe.q_projetos.service.VinculoEquipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vinculos")
@Tag(name = "Vínculo Equipe")
public class VinculoEquipeController {

    @Autowired
    private VinculoEquipeService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos os vínculos (apenas Admin)")
    public ResponseEntity<List<VinculoEquipeResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    @Operation(summary = "Buscar vínculo por ID")
    public ResponseEntity<VinculoEquipeResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/projeto/{projetoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    @Operation(summary = "Listar vínculos de um projeto")
    public ResponseEntity<List<VinculoEquipeResponseDTO>> listarPorProjeto(@PathVariable Long projetoId) {
        return ResponseEntity.ok(service.listarPorProjeto(projetoId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    @Operation(summary = "Criar vínculo de equipe")
    public ResponseEntity<VinculoEquipeResponseDTO> salvar(@Valid @RequestBody VinculoEquipeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    @Operation(summary = "Atualizar vínculo de equipe")
    public ResponseEntity<VinculoEquipeResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody VinculoEquipeDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    @Operation(summary = "Excluir vínculo de equipe")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
