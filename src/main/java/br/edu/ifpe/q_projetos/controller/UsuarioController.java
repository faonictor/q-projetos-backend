package br.edu.ifpe.q_projetos.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.q_projetos.DTO.UsuarioCreateDTO;
import br.edu.ifpe.q_projetos.DTO.UsuarioResponseDTO;
import br.edu.ifpe.q_projetos.DTO.UsuarioUpdateDTO;
import br.edu.ifpe.q_projetos.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO salvar(@Valid @RequestBody UsuarioCreateDTO dto) {
        return service.cadastrarUsuario(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") 
    public List<UsuarioResponseDTO> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        // Como o Service lança exceção se não achar, se chegar aqui é porque existe (HTTP 200)
        UsuarioResponseDTO dto = service.buscarPorId(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto) {
        // Simplificado: A validação de existência e propriedade acontece dentro do Service
        UsuarioResponseDTO atualizado = service.atualizarUsuario(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletarUsuario(id);
    }
}