package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.dto.*;
import br.edu.ifpe.q_projetos.exception.RegraNegocioException;
import br.edu.ifpe.q_projetos.exception.RecursoNaoEncontradoException;
import br.edu.ifpe.q_projetos.model.*;
import br.edu.ifpe.q_projetos.repository.*;
import br.edu.ifpe.q_projetos.security.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjetoService {

    private final ProjetoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final VinculoEquipeRepository vinculoRepository;

    public ProjetoService(
            ProjetoRepository repository,
            UsuarioRepository usuarioRepository,
            VinculoEquipeRepository vinculoRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.vinculoRepository = vinculoRepository;
    }

    // --- LEITURA ---

    public List<ProjetoResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProjetoResponseDTO buscarPorId(Long id) {
        Projeto projeto = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado com o ID: " + id));
        return toResponseDTO(projeto);
    }

    // --- ESCRITA (CREATE) ---

    @Transactional
    public ProjetoResponseDTO salvar(ProjetoCreateDTO dto) {
        validarPermissaoCriacao();
        // 1. Validar cronograma e links (Regra de Negócio)
        validarDatas(dto.getDataInicio(), dto.getDataTermino(), dto.getDataInicioInscricao(),
                dto.getDataFimInscricao());
        validarLinksObrigatorios(dto.getLinkEdital(), dto.getLinkInscricaoExterno());
        validarBase64(dto.getBanner());

        // 2. Criar a entidade Projeto
        Projeto projeto = new Projeto();
        copiarDadosParaEntidade(dto, projeto);
        projeto.setStatusModeracao(Projeto.StatusModeracao.PENDENTE); // RN01

        // 3. Salvar o Projeto para gerar o ID
        Projeto projetoSalvo = repository.save(projeto);

        // 4. Vincular o Coordenador ao projeto recém-criado
        vincularCoordenador(projetoSalvo.getId(), dto.getIdCoordenadorManual());

        return toResponseDTO(projetoSalvo);
    }

    // --- ATUALIZAÇÃO (UPDATE) ---

    @Transactional
    public ProjetoResponseDTO atualizar(Long id, ProjetoUpdateDTO dto) {
        Projeto projeto = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado"));

        // Validação de Segurança: Somente o coordenador ou ADMIN pode editar
        validarPermissaoEdicao(id);

        boolean estruturalAlterado = false;

        // Atualização parcial (Patch style)
        if (dto.getTitulo() != null && !dto.getTitulo().equals(projeto.getTitulo())) {
            projeto.setTitulo(dto.getTitulo());
            estruturalAlterado = true;
        }
        if (dto.getTipo() != null && !dto.getTipo().equals(projeto.getTipo())) {
            projeto.setTipo(dto.getTipo());
            estruturalAlterado = true;
        }
        if (dto.getModalidade() != null && !dto.getModalidade().equals(projeto.getModalidade())) {
            projeto.setModalidade(dto.getModalidade());
            estruturalAlterado = true;
        }

        if (dto.getDescricao() != null)
            projeto.setDescricao(dto.getDescricao());
        if (dto.getVagas() != null)
            projeto.setVagas(dto.getVagas());
        if (dto.getLinkEdital() != null)
            projeto.setLinkEdital(dto.getLinkEdital());
        if (dto.getLinkInscricaoExterno() != null)
            projeto.setLinkInscricaoExterno(dto.getLinkInscricaoExterno());
        if (dto.getBanner() != null) {
            validarBase64(dto.getBanner());
            projeto.setBanner(dto.getBanner());
        }

        if (dto.getDataInicio() != null)
            projeto.setDataInicio(dto.getDataInicio());
        if (dto.getDataTermino() != null)
            projeto.setDataTermino(dto.getDataTermino());
        if (dto.getDataInicioInscricao() != null)
            projeto.setDataInicioInscricao(dto.getDataInicioInscricao());
        if (dto.getDataFimInscricao() != null)
            projeto.setDataFimInscricao(dto.getDataFimInscricao());

        // RN12: Alterações em campos estruturais retornam o projeto ao status PENDENTE
        if (estruturalAlterado) {
            projeto.setStatusModeracao(Projeto.StatusModeracao.PENDENTE);
        }

        return toResponseDTO(repository.save(projeto));
    }

    @Transactional
    public ProjetoResponseDTO aprovarProjeto(Long id) {
        validarPermissaoAdmin();
        Projeto projeto = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado"));
        projeto.setStatusModeracao(Projeto.StatusModeracao.PUBLICADO);
        return toResponseDTO(repository.save(projeto));
    }

    @Transactional
    public ProjetoResponseDTO reprovarProjeto(Long id) {
        validarPermissaoAdmin();
        Projeto projeto = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado"));
        projeto.setStatusModeracao(Projeto.StatusModeracao.REPROVADO);
        return toResponseDTO(repository.save(projeto));
    }

    public void deletar(Long id) {
        validarPermissaoAdmin();
        Projeto projeto = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado com o ID: " + id));

        if (!Projeto.StatusModeracao.REPROVADO.equals(projeto.getStatusModeracao())) {
            throw new RegraNegocioException("Regra de Negócio: Somente projetos com status REPROVADO podem ser excluídos.");
        }

        repository.deleteById(id);
    }

    // --- BUSCAS ESPECÍFICAS ---

    public List<ProjetoResponseDTO> buscarPorTexto(String texto) {
        return repository.findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(texto, texto)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetoResponseDTO> buscarPorTipo(Projeto.TipoProjeto tipo) {
        return repository.findByTipo(tipo)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetoResponseDTO> buscarPorStatus(ProjetoResponseDTO.StatusInscricao status) {
        LocalDate hoje = LocalDate.now();
        List<Projeto> projetos;

        switch (status) {
            case ABERTA:
                projetos = repository.findProjetosComInscricoesAbertas(hoje);
                break;
            case ENCERRADA:
                projetos = repository.findProjetosComInscricoesEncerradas(hoje);
                break;
            case AGUARDANDO:
                projetos = repository.findProjetosAguardandoInscricao(hoje);
                break;
            default:
                throw new RuntimeException("Status de busca inválido.");
        }

        return projetos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ProjetoResponseDTO> listarMeusProjetos() {
        Usuario logado = SecurityUtils.getLoggedUser(usuarioRepository);
        return repository.findProjetosByCoordenador(logado.getId()).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // --- MÉTODOS AUXILIARES E REGRAS DE NEGÓCIO ---

    private void validarDatas(LocalDate inicio, LocalDate fim, LocalDate inscInicio, LocalDate inscFim) {
        if (fim.isBefore(inicio)) {
            throw new RegraNegocioException("Regra de Negócio: A data de término não pode ser anterior ao início.");
        }
        if (inscFim.isBefore(inscInicio)) {
            throw new RegraNegocioException("Regra de Negócio: O fim das inscrições não pode ser anterior ao início.");
        }
    }

    private void validarLinksObrigatorios(String linkEdital, String linkInscricao) {
        if ((linkEdital == null || linkEdital.isBlank()) && (linkInscricao == null || linkInscricao.isBlank())) {
            throw new RegraNegocioException(
                    "Regra de Negócio: Pelo menos um link (Edital ou Inscrição Externo) deve ser fornecido.");
        }
    }

    private void validarBase64(String base64) {
        if (base64 == null || base64.isBlank())
            return;

        if (!base64.startsWith("data:image/")) {
            throw new RegraNegocioException("Regra de Negócio: Formato de imagem inválido. Deve ser Base64 (data:image/...).");
        }

        if (base64.length() > 2800000) {
            throw new RegraNegocioException("Regra de Negócio: O banner excede o tamanho máximo de 2MB.");
        }
    }

    private void copiarDadosParaEntidade(ProjetoCreateDTO dto, Projeto projeto) {
        projeto.setTitulo(dto.getTitulo());
        projeto.setTipo(dto.getTipo());
        projeto.setDescricao(dto.getDescricao());
        projeto.setDataInicio(dto.getDataInicio());
        projeto.setDataTermino(dto.getDataTermino());
        projeto.setDataInicioInscricao(dto.getDataInicioInscricao());
        projeto.setDataFimInscricao(dto.getDataFimInscricao());
        projeto.setLinkEdital(dto.getLinkEdital());
        projeto.setLinkInscricaoExterno(dto.getLinkInscricaoExterno());
        projeto.setVagas(dto.getVagas());
        projeto.setModalidade(dto.getModalidade());
        projeto.setBanner(dto.getBanner());
    }

    private void vincularCoordenador(Long projetoId, Long idCoordenadorManual) {
        Usuario logado = SecurityUtils.getLoggedUser(usuarioRepository);

        Long idFinalDoCoordenador = logado.getId();
        Usuario coordenadorFinal = logado;

        boolean isAdmin = SecurityUtils.isAdmin();

        if (idCoordenadorManual != null && isAdmin) {
            idFinalDoCoordenador = idCoordenadorManual;
            coordenadorFinal = usuarioRepository.findById(idCoordenadorManual)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Coordenador manual não encontrado."));
        }

        // Validação de Segurança: O coordenador de um projeto DEVE ser um SERVIDOR
        if (!Usuario.Vinculo.SERVIDOR.equals(coordenadorFinal.getVinculo())) {
            throw new RegraNegocioException("Regra de Negócio: Apenas usuários com vínculo SERVIDOR podem ser coordenadores de projetos.");
        }

        VinculoEquipe vinculo = new VinculoEquipe();
        vinculo.setIdProjeto(projetoId);
        vinculo.setIdUsuario(idFinalDoCoordenador);
        vinculo.setPapel(VinculoEquipe.Papel.COORDENADOR);
        vinculo.setAtivo(true);

        vinculoRepository.save(vinculo);
    }

    private void validarPermissaoEdicao(Long projetoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RecursoNaoEncontradoException("Acesso negado: É necessário estar logado para realizar esta ação.");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        Usuario logado = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário logado não encontrado no banco de dados."));

        VinculoEquipe vinculo = vinculoRepository.findByIdProjetoAndIdUsuario(projetoId, logado.getId())
                .orElseThrow(
                        () -> new RecursoNaoEncontradoException("Acesso negado: Você não possui permissão sobre este projeto."));

        if (!VinculoEquipe.Papel.COORDENADOR.equals(vinculo.getPapel())) {
            throw new RecursoNaoEncontradoException("Acesso negado: Apenas o COORDENADOR possui permissão para alterar o projeto.");
        }

        if (!Boolean.TRUE.equals(vinculo.getAtivo())) {
            throw new RecursoNaoEncontradoException("Acesso negado: Seu vínculo com este projeto está inativo.");
        }
    }

    private void validarPermissaoCriacao() {
        Usuario logado = SecurityUtils.getLoggedUser(usuarioRepository);

        if (!SecurityUtils.isAdmin()) {
            if (logado.getVinculo() == null || !"SERVIDOR".equalsIgnoreCase(logado.getVinculo().toString())) {
                throw new RegraNegocioException(
                        "Acesso negado: Apenas Administradores ou Servidores possuem permissão para cadastrar projetos.");
            }
        }
    }

    private void validarPermissaoAdmin() {
        SecurityUtils.validarPermissaoAdmin();
    }

    public ProjetoResponseDTO toResponseDTO(Projeto projeto) {
        LocalDate hoje = LocalDate.now();
        ProjetoResponseDTO.StatusInscricao status;

        if (projeto.getDataInicioInscricao() == null || projeto.getDataFimInscricao() == null) {
            status = ProjetoResponseDTO.StatusInscricao.ENCERRADA;
        } else if (hoje.isBefore(projeto.getDataInicioInscricao())) {
            status = ProjetoResponseDTO.StatusInscricao.AGUARDANDO;
        } else if (hoje.isAfter(projeto.getDataFimInscricao())) {
            status = ProjetoResponseDTO.StatusInscricao.ENCERRADA;
        } else {
            status = ProjetoResponseDTO.StatusInscricao.ABERTA;
        }

        return ProjetoResponseDTO.builder()
                .id(projeto.getId())
                .titulo(projeto.getTitulo())
                .tipo(projeto.getTipo())
                .descricao(projeto.getDescricao())
                .dataCriacao(projeto.getDataCriacao())
                .dataInicio(projeto.getDataInicio())
                .dataTermino(projeto.getDataTermino())
                .dataInicioInscricao(projeto.getDataInicioInscricao())
                .dataFimInscricao(projeto.getDataFimInscricao())
                .linkEdital(projeto.getLinkEdital())
                .linkInscricaoExterno(projeto.getLinkInscricaoExterno())
                .vagas(projeto.getVagas())
                .modalidade(projeto.getModalidade())
                .banner(projeto.getBanner())
                .status(status)
                .statusModeracao(projeto.getStatusModeracao())
                .build();
    }
}
