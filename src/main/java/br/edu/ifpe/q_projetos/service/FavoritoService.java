package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.dto.FavoritoDTO;
import br.edu.ifpe.q_projetos.dto.FavoritoResponseDTO;
import br.edu.ifpe.q_projetos.exception.RegraNegocioException;
import br.edu.ifpe.q_projetos.exception.RecursoNaoEncontradoException;
import br.edu.ifpe.q_projetos.model.Favorito;
import br.edu.ifpe.q_projetos.model.Projeto;
import br.edu.ifpe.q_projetos.model.Usuario;
import br.edu.ifpe.q_projetos.repository.FavoritoRepository;
import br.edu.ifpe.q_projetos.repository.ProjetoRepository;
import br.edu.ifpe.q_projetos.repository.UsuarioRepository;
import br.edu.ifpe.q_projetos.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository repository;
    
    @Autowired
    private ProjetoRepository projetoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public FavoritoResponseDTO vincularFavorito(FavoritoDTO dto) {
        Long idUsuario = getLoggedUserId();
        
        Favorito favorito = repository.findByIdUsuarioAndIdProjeto(idUsuario, dto.getIdProjeto())
                .orElseGet(() -> repository.save(new Favorito(idUsuario, dto.getIdProjeto())));
        
        return toResponseDTO(favorito);
    }

    @Transactional
    public void desvincularFavorito(Long idProjeto) {
        Long idUsuario = getLoggedUserId();
        repository.findByIdUsuarioAndIdProjeto(idUsuario, idProjeto)
                .ifPresent(repository::delete);
    }

    public List<FavoritoResponseDTO> listarMeuHistorico() {
        Long idUsuario = getLoggedUserId();
        return repository.findByIdUsuario(idUsuario).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<FavoritoResponseDTO> listarTodos() {
        SecurityUtils.validarPermissaoAdmin();
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public FavoritoResponseDTO buscarPorId(Long id) {
        Favorito favorito = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Favorito não encontrado"));

        Long logadoId = getLoggedUserId();
        if (!favorito.getIdUsuario().equals(logadoId)) {
            SecurityUtils.validarPermissaoAdmin();
        }

        return toResponseDTO(favorito);
    }

    private Long getLoggedUserId() {
        return SecurityUtils.getLoggedUserId(usuarioRepository);
    }

    private void validarPermissaoAdmin() {
        SecurityUtils.validarPermissaoAdmin();
    }

    private FavoritoResponseDTO toResponseDTO(Favorito favorito) {
        Projeto projeto = projetoRepository.findById(favorito.getIdProjeto())
                .orElse(null);
        
        return FavoritoResponseDTO.builder()
                .id(favorito.getId())
                .idUsuario(favorito.getIdUsuario())
                .idProjeto(favorito.getIdProjeto())
                .tituloProjeto(projeto != null ? projeto.getTitulo() : "Projeto não encontrado")
                .bannerProjeto(projeto != null ? projeto.getBanner() : null)
                .dataRegistro(favorito.getDataRegistro())
                .build();
    }
}
