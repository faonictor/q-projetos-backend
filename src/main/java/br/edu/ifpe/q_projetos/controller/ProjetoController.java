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
@CrossOrigin(origins = "*") // Permite que o Front-end (Ionic) consuma esta API
public class ProjetoController {

    // 1. ISOLAMENTO: Injetamos APENAS o Service.
    // O Controller não faz ideia de que existe um MySQL ou um Repository por trás.
    @Autowired
    private ProjetoService service;

    // ----------------------------------------------------------------------
    // ROTAS DE LIGAÇÃO COM O FRONT-END
    // O Controller lida apenas com HTTP (Status de resposta, URLs, JSON)
    // ----------------------------------------------------------------------

    @GetMapping
    public List<Projeto> listar() {
        // Delega o trabalho de ir buscar a lista ao Service
        return service.listarTodos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Retorna um 201 (Created) se tudo correr bem
    public Projeto criar(@RequestBody Projeto projeto) {
        // Delega a validação e a gravação ao Service
        return service.salvar(projeto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscar(@PathVariable Long id) {
        // O Service tenta encontrar o projeto. 
        // Se encontrar, o Controller devolve um 200 (OK). Se não, devolve 404 (Not Found).
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna um 204 (No Content) após apagar
    public void remover(@PathVariable Long id) {
        // Delega o processo de exclusão ao Service
        service.excluir(id);
    }
}