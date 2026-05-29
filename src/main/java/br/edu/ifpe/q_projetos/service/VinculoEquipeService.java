package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.dto.VinculoEquipeDTO;
import br.edu.ifpe.q_projetos.dto.VinculoEquipeResponseDTO;
import br.edu.ifpe.q_projetos.model.*;
import br.edu.ifpe.q_projetos.repository.*;
import br.edu.ifpe.q_projetos.exception.RecursoNaoEncontradoException;
import br.edu.ifpe.q_projetos.exception.RegraNegocioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VinculoEquipeService {

    @Autowired
    private VinculoEquipeRepository repository;
    
    @Autowired
    private ProjetoRepository projetoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public VinculoEquipeResponseDTO salvar(VinculoEquipeDTO dto) {
        validarCoordenador(dto.getIdProjeto());

        // Regra de Negócio: Um usuário só pode ter 1 papel por projeto
        if (repository.findByIdProjetoAndIdUsuario(dto.getIdProjeto(), dto.getIdUsuario()).isPresent()) {
            throw new RegraNegocioException("O usuário já possui um vínculo com este projeto. Atualize o vínculo existente se necessário.");
        }

        VinculoEquipe vinculo = new VinculoEquipe();
        vinculo.setIdProjeto(dto.getIdProjeto());
        vinculo.setIdUsuario(dto.getIdUsuario());
        vinculo.setPapel(dto.getPapel());
        vinculo.setAtivo(dto.getAtivo());

        return toResponseDTO(repository.save(vinculo));
    }

    public List<VinculoEquipeResponseDTO> listar() {
        validarPermissaoAdmin();
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<VinculoEquipeResponseDTO> listarPorProjeto(Long projetoId) {
        validarCoordenador(projetoId);
        return repository.findByIdProjeto(projetoId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public VinculoEquipeResponseDTO buscarPorId(Long id) {
        VinculoEquipe vinculo = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vínculo não encontrado"));
        
        validarCoordenador(vinculo.getIdProjeto());
        return toResponseDTO(vinculo);
    }

    @Transactional
    public VinculoEquipeResponseDTO atualizar(Long id, VinculoEquipeDTO dto) {
        VinculoEquipe vinculo = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vínculo não encontrado"));

        validarCoordenador(vinculo.getIdProjeto());

        vinculo.setPapel(dto.getPapel());
        vinculo.setAtivo(dto.getAtivo());

        return toResponseDTO(repository.save(vinculo));
    }

    @Transactional
    public void deletar(Long id) {
        VinculoEquipe vinculo = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Vínculo não encontrado"));

        validarCoordenador(vinculo.getIdProjeto());
        repository.deleteById(id);
    }

    private void validarCoordenador(Long idProjeto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RegraNegocioException("Acesso negado: Usuário não autenticado.");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return;

        Usuario logado = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário logado não encontrado"));

        VinculoEquipe vinculo = repository.findByIdProjetoAndIdUsuario(idProjeto, logado.getId())
                .orElseThrow(() -> new RegraNegocioException("Acesso negado: Você não possui vínculo neste projeto"));

        if (!VinculoEquipe.Papel.COORDENADOR.equals(vinculo.getPapel())) {
            throw new RegraNegocioException("Acesso negado: Apenas coordenadores podem gerenciar membros");
        }

        if (!Boolean.TRUE.equals(vinculo.getAtivo())) {
            throw new RegraNegocioException("Acesso negado: Seu vínculo de coordenação está inativo");
        }
    }

    private void validarPermissaoAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new RegraNegocioException("Acesso negado: Ação exclusiva para administradores.");
        }
    }

    private VinculoEquipeResponseDTO toResponseDTO(VinculoEquipe vinculo) {
        Projeto projeto = projetoRepository.findById(vinculo.getIdProjeto()).orElse(null);
        Usuario usuario = usuarioRepository.findById(vinculo.getIdUsuario()).orElse(null);

        return VinculoEquipeResponseDTO.builder()
                .id(vinculo.getId())
                .idProjeto(vinculo.getIdProjeto())
                .tituloProjeto(projeto != null ? projeto.getTitulo() : "Projeto não encontrado")
                .idUsuario(vinculo.getIdUsuario())
                .nomeUsuario(usuario != null ? usuario.getNome() : "Usuário não encontrado")
                .emailUsuario(usuario != null ? usuario.getEmail() : null)
                .papel(vinculo.getPapel())
                .ativo(vinculo.getAtivo())
                .build();
    }
}
