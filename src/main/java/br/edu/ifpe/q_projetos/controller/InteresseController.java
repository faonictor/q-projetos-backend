package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.dto.InteresseRequestDTO;
import br.edu.ifpe.q_projetos.dto.InteresseResponseDTO;
import br.edu.ifpe.q_projetos.service.InteresseService;

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

    @PostMapping
    public InteresseResponseDTO salvar(@RequestBody InteresseRequestDTO dto) {
        return service.salvar(dto);
    }

    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    @GetMapping
    public List<InteresseResponseDTO> listar(
            @RequestParam Long usuarioLogado,
            @RequestParam boolean isAdmin) {

        return service.listarTodos(usuarioLogado, isAdmin);
    }

    @GetMapping("/{id}")
    public InteresseResponseDTO buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public InteresseResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody InteresseRequestDTO dto) {

        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }
}