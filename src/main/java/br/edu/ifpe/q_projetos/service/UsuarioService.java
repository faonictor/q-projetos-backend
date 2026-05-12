
package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.model.Usuario;
import br.edu.ifpe.q_projetos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository repository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
     
    public Usuario salvar(Usuario usuario) {
        String senhaComHash = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaComHash);
        
        return repository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return repository.findAll();
    }

    public Usuario buscarPorId(Long id) {
      
        return repository.findById(id).orElse(null);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        return repository.findById(id).map(usuario -> {
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            
            // Verifica se a senha enviada é diferente da atual antes de aplicar novo  hash 
            if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
                String senhaComHash = passwordEncoder.encode(usuarioAtualizado.getSenha());
                usuario.setSenha(senhaComHash);
            }
            
            return repository.save(usuario);
        }).orElse(null); 
        // Você também pode lançar uma exceção personalizada aqui caso o ID não exista
    }

    
}

