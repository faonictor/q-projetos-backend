package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.dto.InteresseDTO;
import br.edu.ifpe.q_projetos.dto.InteresseResponseDTO;
import br.edu.ifpe.q_projetos.service.InteresseService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interesses")
public class InteresseController {

    private final InteresseService service;

    public InteresseController(InteresseService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    public List<InteresseResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    public InteresseResponseDTO buscarPorId(
            @PathVariable Long id) {

        return service.buscarPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    public InteresseResponseDTO salvar(
            @RequestBody InteresseDTO dto) {

        return service.salvar(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    public InteresseResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody InteresseDTO dto) {

        return service.atualizar(id, dto);
    }

    @GetMapping("/projeto/{projetoId}")
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    public ResponseEntity<List<InteresseResponseDTO>>
            listarLeadsPorProjeto(
                    @PathVariable Long projetoId) {

        return ResponseEntity.ok(
                service.listarLeadsPorProjeto(projetoId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}