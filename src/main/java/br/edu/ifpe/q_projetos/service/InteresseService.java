package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.dto.InteresseDTO;
import br.edu.ifpe.q_projetos.dto.InteresseResponseDTO;
import br.edu.ifpe.q_projetos.model.*;
import br.edu.ifpe.q_projetos.repository.*;
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
            throw new RuntimeException("Regra de Negócio: Você já manifestou interesse neste projeto.");
        }

        validarLgpd(dto.getAceitouLgpd());

        Projeto projeto = projetoRepository.findById(dto.getIdProjeto())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));

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
        validarPermissaoAdmin();
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
                .orElseThrow(() -> new RuntimeException("Interesse não encontrado."));
        validarPermissaoAcessoProjeto(interesse.getProjeto().getId());
        return toResponseDTO(interesse);
    }

    @Transactional
    public void deletar(Long id) {
        Interesse interesse = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Interesse não encontrado."));
        validarPermissaoAcessoProjeto(interesse.getProjeto().getId());
        repository.delete(interesse);
    }

    private void validarLgpd(Boolean aceitou) {
        if (aceitou == null || !aceitou) {
            throw new RuntimeException("Regra de Negócio: É obrigatório aceitar os termos da LGPD.");
        }
    }

    private void validarPermissaoAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new RuntimeException("Acesso negado: Ação exclusiva para administradores.");
        }
    }

    private void validarPermissaoAcessoProjeto(Long projetoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("Acesso negado: Usuário não autenticado.");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin)
            return;

        Usuario logado = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado."));

        // RN05: Restrição de Coordenação Isolada
        VinculoEquipe vinculo = vinculoRepository.findByIdProjetoAndIdUsuario(projetoId, logado.getId())
                .orElseThrow(() -> new RuntimeException("Acesso negado: Você não possui vínculo com este projeto."));

        if (!VinculoEquipe.Papel.COORDENADOR.equals(vinculo.getPapel()) &&
                !VinculoEquipe.Papel.COLABORADOR.equals(vinculo.getPapel())) {
            throw new RuntimeException("Acesso negado: Apenas coordenadores ou colaboradores podem visualizar leads.");
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
