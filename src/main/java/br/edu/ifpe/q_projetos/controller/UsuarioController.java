
package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.model.Usuario;
import br.edu.ifpe.q_projetos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/usuarios")


public class UsuarioController {
    @Autowired
    private UsuarioService service;

    @PostMapping
    public Usuario salvar(@RequestBody Usuario usuario) {
        return service.salvar(usuario);
    }

    @GetMapping
    public List<Usuario> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public Usuario buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
    return service.atualizar(id, usuario);
}
}