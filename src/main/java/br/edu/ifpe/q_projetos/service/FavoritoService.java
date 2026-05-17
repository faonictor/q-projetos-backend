package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.model.Favorito;
import br.edu.ifpe.q_projetos.repository.FavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    // Vincular projeto aos favoritos
    public Favorito vincularFavorito(Long idUsuario, Long idProjeto) {
        return favoritoRepository.findByIdUsuarioAndIdProjeto(idUsuario, idProjeto)
                .orElseGet(() -> favoritoRepository.save(new Favorito(idUsuario, idProjeto)));
    }

    // Desvincular projeto dos favoritos
    public void desvincularFavorito(Long idUsuario, Long idProjeto) {
        favoritoRepository.findByIdUsuarioAndIdProjeto(idUsuario, idProjeto)
                .ifPresent(favoritoRepository::delete);
    }

    // Histórico do estudante
    public List<Favorito> listarHistorico(Long idUsuario) {
        return favoritoRepository.findByIdUsuario(idUsuario);
    }

    // CRUD padrão
    public Favorito salvar(Favorito favorito) {
        return favoritoRepository.save(favorito);
    }

    public List<Favorito> listarTodos() {
        return favoritoRepository.findAll();
    }

    public Favorito buscarPorId(Long id) {
        return favoritoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorito não encontrado"));
    }

    public Favorito atualizar(Long id, Favorito favoritoAtualizado) {
        return favoritoRepository.findById(id).map(favorito -> {
            favorito.setIdUsuario(favoritoAtualizado.getIdUsuario());
            favorito.setIdProjeto(favoritoAtualizado.getIdProjeto());
            return favoritoRepository.save(favorito);
        }).orElseThrow(() -> new RuntimeException("Favorito não encontrado"));
    }

    public void deletar(Long id) {
        favoritoRepository.deleteById(id);
    }
}
