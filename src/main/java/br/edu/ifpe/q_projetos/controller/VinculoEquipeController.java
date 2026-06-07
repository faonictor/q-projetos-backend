package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.model.VinculoEquipe;
import br.edu.ifpe.q_projetos.service.VinculoEquipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vinculos")
@Tag(name = "Vínculo Equipe")
public class VinculoEquipeController {

    private final VinculoEquipeService service;

    public VinculoEquipeController(
            VinculoEquipeService service
    ) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    @Operation(summary = "Criar vínculo")
    public VinculoEquipe criar(
            @RequestBody VinculoEquipe vinculo
    ) {

        return service.salvar(vinculo);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    @Operation(summary = "Listar vínculos")
    public List<VinculoEquipe> listar() {

        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    @Operation(summary = "Buscar vínculo por ID")
    public VinculoEquipe buscar(
            @PathVariable Long id
    ) {

        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    @Operation(summary = "Atualizar vínculo")
    public VinculoEquipe atualizar(
            @PathVariable Long id,
            @RequestBody VinculoEquipe vinculo
    ) {

        return service.atualizar(
                id,
                vinculo
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    @Operation(summary = "Excluir vínculo")
    public void deletar(
            @PathVariable Long id
    ) {

        service.deletar(id);
    }
}