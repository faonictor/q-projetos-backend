package br.edu.ifpe.q_projetos.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.q_projetos.dto.ProjetoCreateDTO;
import br.edu.ifpe.q_projetos.dto.ProjetoResponseDTO;
import br.edu.ifpe.q_projetos.dto.ProjetoUpdateDTO;
import br.edu.ifpe.q_projetos.model.Projeto.TipoProjeto;
import br.edu.ifpe.q_projetos.service.ProjetoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projetos")
@CrossOrigin(origins = "*")
public class ProjetoController {

    @Autowired
    private ProjetoService service;

    @GetMapping
    public ResponseEntity<List<ProjetoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public ResponseEntity<ProjetoResponseDTO> criar(@Valid @RequestBody ProjetoCreateDTO dto) {
        ProjetoResponseDTO projetoSalvo = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoSalvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public ResponseEntity<ProjetoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProjetoUpdateDTO dto
    ) {
        ProjetoResponseDTO projetoAtualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(projetoAtualizado);
    }

    @PostMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjetoResponseDTO> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(service.aprovarProjeto(id));
    }

    @PostMapping("/{id}/reprovar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjetoResponseDTO> reprovar(@PathVariable Long id) {
        return ResponseEntity.ok(service.reprovarProjeto(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    // --- ENDPOINTS DE BUSCA ---

    @GetMapping("/buscar")
    public ResponseEntity<List<ProjetoResponseDTO>> buscarPorTexto(@RequestParam("texto") String texto) {
        return ResponseEntity.ok(service.buscarPorTexto(texto));
    }

    @GetMapping("/meus-projetos")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORD')")
    public ResponseEntity<List<ProjetoResponseDTO>> listarMeusProjetos() {
        return ResponseEntity.ok(service.listarMeusProjetos());
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<ProjetoResponseDTO>> buscarPorTipo(@RequestParam("tipo") TipoProjeto tipo) {
        return ResponseEntity.ok(service.buscarPorTipo(tipo));
    }

    @GetMapping("/status")
    public ResponseEntity<List<ProjetoResponseDTO>> buscarPorStatus(
            @RequestParam ProjetoResponseDTO.StatusInscricao status
    ) {
        return ResponseEntity.ok(service.buscarPorStatus(status));
    }
}
