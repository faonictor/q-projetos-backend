package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.dto.InteresseDTO;
import br.edu.ifpe.q_projetos.dto.InteresseResponseDTO;
import br.edu.ifpe.q_projetos.exception.RegraNegocioException;
import br.edu.ifpe.q_projetos.exception.RecursoNaoEncontradoException;
import br.edu.ifpe.q_projetos.model.*;
import br.edu.ifpe.q_projetos.repository.*;
import br.edu.ifpe.q_projetos.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InteresseService {

    @Autowired
    private InteresseRepository repository;
    
    @Autowired
    private ProjetoRepository projetoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private VinculoEquipeRepository vinculoRepository;

    @Transactional
    public InteresseResponseDTO salvar(InteresseDTO dto) {
        // RN02: Unicidade de interesse
        if (repository.existsByEmailAndProjetoId(dto.getEmail(), dto.getIdProjeto())) {
            throw new RegraNegocioException("Regra de Negócio: Você já manifestou interesse neste projeto.");
        }

        validarLgpd(dto.getAceitouLgpd());

        Projeto projeto = projetoRepository.findById(dto.getIdProjeto())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado."));

        Interesse interesse = new Interesse();
        interesse.setProjeto(projeto);
        interesse.setNome(dto.getNome());
        interesse.setEmail(dto.getEmail());
        interesse.setSeriePeriodo(dto.getSeriePeriodo());
        interesse.setModalidadePretendida(dto.getModalidadePretendida());
        interesse.setAceitouLgpd(dto.getAceitouLgpd());

        return toResponseDTO(repository.save(interesse));
    }

    public List<InteresseResponseDTO> listarTodos() {
        SecurityUtils.validarPermissaoAdmin();
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<InteresseResponseDTO> listarLeadsPorProjeto(Long projetoId) {
        validarPermissaoAcessoProjeto(projetoId);
        return repository.findByProjetoId(projetoId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public InteresseResponseDTO buscarPorId(Long id) {
        Interesse interesse = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Interesse não encontrado."));
        validarPermissaoAcessoProjeto(interesse.getProjeto().getId());
        return toResponseDTO(interesse);
    }

    @Transactional
    public void deletar(Long id) {
        Interesse interesse = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Interesse não encontrado."));
        validarPermissaoAcessoProjeto(interesse.getProjeto().getId());
        repository.delete(interesse);
    }

    private void validarLgpd(Boolean aceitou) {
        if (aceitou == null || !aceitou) {
            throw new RegraNegocioException("Regra de Negócio: É obrigatório aceitar os termos da LGPD.");
        }
    }

    private void validarPermissaoAcessoProjeto(Long projetoId) {
        if (SecurityUtils.isAdmin())
            return;

        Usuario logado = SecurityUtils.getLoggedUser(usuarioRepository);

        // RN05: Restrição de Coordenação Isolada
        VinculoEquipe vinculo = vinculoRepository.findByIdProjetoAndIdUsuario(projetoId, logado.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Acesso negado: Você não possui vínculo com este projeto."));

        if (!VinculoEquipe.Papel.COORDENADOR.equals(vinculo.getPapel()) &&
                !VinculoEquipe.Papel.COLABORADOR.equals(vinculo.getPapel())) {
            throw new RecursoNaoEncontradoException("Acesso negado: Apenas coordenadores ou colaboradores podem visualizar leads.");
        }
    }

    private InteresseResponseDTO toResponseDTO(Interesse interesse) {
        return InteresseResponseDTO.builder()
                .id(interesse.getId())
                .idProjeto(interesse.getProjeto().getId())
                .tituloProjeto(interesse.getProjeto().getTitulo())
                .nome(interesse.getNome())
                .email(interesse.getEmail())
                .seriePeriodo(interesse.getSeriePeriodo())
                .modalidadePretendida(interesse.getModalidadePretendida())
                .aceitouLgpd(interesse.getAceitouLgpd())
                .dataRegistro(interesse.getDataRegistro())
                .build();
    }
}
