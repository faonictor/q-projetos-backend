package br.edu.ifpe.q_projetos.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import br.edu.ifpe.q_projetos.DTO.ProjetoCreateDTO;
import br.edu.ifpe.q_projetos.DTO.ProjetoResponseDTO;
import br.edu.ifpe.q_projetos.DTO.ProjetoUpdateDTO;
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
        // O Optional foi removido aqui porque o Service já lança exceção se não achar
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProjetoResponseDTO> criar(@Valid @RequestBody ProjetoCreateDTO dto) {
        // O @Valid aciona as validações que configuramos no DTO
        ProjetoResponseDTO projetoSalvo = service.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProjetoUpdateDTO dto
    ) {
        ProjetoResponseDTO projetoAtualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(projetoAtualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    // --- ENDPOINTS DE BUSCA ---

    @GetMapping("/buscar")
    public ResponseEntity<List<ProjetoResponseDTO>> buscarPorTexto(@RequestParam("texto") String texto) {
        // Nota: O método no Service precisará ser ajustado para retornar ProjetoResponseDTO
        return ResponseEntity.ok(service.buscarPorTexto(texto));
    }

    @GetMapping("/tipo")
    public ResponseEntity<List<ProjetoResponseDTO>> buscarPorTipo(@RequestParam("tipo") TipoProjeto tipo) {
        // Nota: O método no Service precisará ser ajustado para retornar ProjetoResponseDTO
        return ResponseEntity.ok(service.buscarPorTipo(tipo));
    }

    @GetMapping("/status")
    public ResponseEntity<List<ProjetoResponseDTO>> buscarPorStatus(
            @RequestParam ProjetoResponseDTO.StatusInscricao status
    ) {
        // Como o status agora é dinâmico (não está mais no banco), este endpoint 
        // chamará a lógica correspondente no Service baseada nas datas.
        return ResponseEntity.ok(service.buscarPorStatus(status));
    }
}