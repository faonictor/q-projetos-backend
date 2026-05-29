package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.dto.InteresseDTO;
import br.edu.ifpe.q_projetos.dto.InteresseResponseDTO;
import br.edu.ifpe.q_projetos.service.InteresseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interesses")
public class InteresseController {

    @Autowired
    private InteresseService service;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<InteresseResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public ResponseEntity<InteresseResponseDTO> buscarPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InteresseResponseDTO> salvar(@Valid @RequestBody InteresseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dto));
    }

    @GetMapping("/projeto/{projetoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public ResponseEntity<List<InteresseResponseDTO>> listarLeadsPorProjeto(
            @PathVariable("projetoId") Long projetoId) {
        return ResponseEntity.ok(service.listarLeadsPorProjeto(projetoId));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}
