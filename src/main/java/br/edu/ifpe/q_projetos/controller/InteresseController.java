package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.model.Interesse;
import br.edu.ifpe.q_projetos.service.InteresseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interesses")
public class InteresseController {

    @Autowired
    private InteresseService service;

    @PostMapping
    public Interesse salvar(@RequestBody Interesse interesse) {
        return service.salvar(interesse);
    }

    @GetMapping
    public List<Interesse> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/coordenador/{responsavel}")
    public ResponseEntity<List<Interesse>> listarLeadsCoordenador(
            @PathVariable String responsavel) {

        List<Interesse> leads =
                service.listarLeadsPorCoordenador(responsavel);

        return ResponseEntity.ok(leads);
    }
}
