package br.edu.ifpe.q_projetos.controller;
import br.edu.ifpe.q_projetos.model.Projeto;
import br.edu.ifpe.q_projetos.service.ProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
@CrossOrigin(origins = "*")
public class ProjetoController {

    @Autowired
    private ProjetoService service;

    @GetMapping
    public List<Projeto> listar() {

        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscarPorId(
            @PathVariable Long id
    ) {

        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

   
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Projeto criar(
            @RequestBody Projeto projeto
    ) {

        return service.salvar(projeto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Projeto> atualizar(
            @PathVariable Long id,
            @RequestBody Projeto projeto
    ) {

        try {

            Projeto projetoAtualizado =
                    service.atualizar(id, projeto);

            return ResponseEntity.ok(projetoAtualizado);

        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(
            @PathVariable Long id
    ) {

        service.deletar(id);
    }

    
    @GetMapping("/buscar")
    public List<Projeto> buscarPorTexto(
            @RequestParam String texto
    ) {

        return service.buscarPorTexto(texto);
    }

  
    @GetMapping("/tipo")
    public List<Projeto> buscarPorTipo(
            @RequestParam Projeto.TipoProjeto tipo
    ) {

        return service.buscarPorTipo(tipo);
    }


    @GetMapping("/status")
    public List<Projeto> buscarPorStatus(
            @RequestParam Projeto.StatusInscricao status
    ) {

        return service.buscarPorStatus(status);
    }
}