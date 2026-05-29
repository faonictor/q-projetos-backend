package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.dto.*;
import br.edu.ifpe.q_projetos.model.*;
import br.edu.ifpe.q_projetos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VinculoEquipeRepository vinculoRepository;

    // --- LEITURA ---

    public List<ProjetoResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ProjetoResponseDTO buscarPorId(Long id) {
        Projeto projeto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado com o ID: " + id));
        return toResponseDTO(projeto);
    }

    // --- ESCRITA (CREATE) ---

    @Transactional
    public ProjetoResponseDTO salvar(ProjetoCreateDTO dto) {
        validarPermissaoCriacao();
        // 1. Validar cronograma (Regra de Negócio)
        validarDatas(dto.getDataInicio(), dto.getDataTermino(), dto.getDataInicioInscricao(),
                dto.getDataFimInscricao());

        // 2. Criar a entidade Projeto
        Projeto projeto = new Projeto();
        copiarDadosParaEntidade(dto, projeto);

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
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        // Validação de Segurança: Somente o coordenador ou ADMIN pode editar
        validarPermissaoEdicao(id);

        // Atualização parcial (Patch style)
        if (dto.getTitulo() != null)
            projeto.setTitulo(dto.getTitulo());
        if (dto.getDescricao() != null)
            projeto.setDescricao(dto.getDescricao());
        if (dto.getVagas() != null)
            projeto.setVagas(dto.getVagas());
        if (dto.getTipo() != null)
            projeto.setTipo(dto.getTipo());
        if (dto.getModalidade() != null)
            projeto.setModalidade(dto.getModalidade());
        if (dto.getLinkEdital() != null)
            projeto.setLinkEdital(dto.getLinkEdital());
        if (dto.getBanner() != null)
            projeto.setBanner(dto.getBanner());

        if (dto.getDataInicio() != null)
            projeto.setDataInicio(dto.getDataInicio());
        if (dto.getDataTermino() != null)
            projeto.setDataTermino(dto.getDataTermino());

        return toResponseDTO(repository.save(projeto));
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Projeto não encontrado com o ID: " + id);
        }

        validarPermissaoEdicao(id);
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

    // --- MÉTODOS AUXILIARES E REGRAS DE NEGÓCIO ---

    private void validarDatas(LocalDate inicio, LocalDate fim, LocalDate inscInicio, LocalDate inscFim) {
        if (fim.isBefore(inicio)) {
            throw new RuntimeException("Regra de Negócio: A data de término não pode ser anterior ao início.");
        }
        if (inscFim.isBefore(inscInicio)) {
            throw new RuntimeException("Regra de Negócio: O fim das inscrições não pode ser anterior ao início.");
        }
    }

    private void vincularCoordenador(Long projetoId, Long idCoordenadorManual) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Bloqueio rígido: Se cair aqui sem autenticação real, interrompe o processo.
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("Acesso negado: É necessário estar logado para criar um projeto.");
        }

        Usuario logado = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados."));

        Long idFinalDoCoordenador = logado.getId();

        // Mantida a lógica de Admin para atribuir o projeto a outra pessoa, caso venha
        // no DTO
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (idCoordenadorManual != null && isAdmin) {
            idFinalDoCoordenador = idCoordenadorManual;
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
            throw new RuntimeException("Acesso negado: É necessário estar logado para realizar esta ação.");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        Usuario logado = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados."));

        // O pulo do gato está aqui: se o repositório retornar vazio, significa que o
        // USER não é dono/coordenador do projeto
        VinculoEquipe vinculo = vinculoRepository.findByIdProjetoAndIdUsuario(projetoId, logado.getId())
                .orElseThrow(
                        () -> new RuntimeException("Acesso negado: Você não possui permissão sobre este projeto."));

        if (!VinculoEquipe.Papel.COORDENADOR.equals(vinculo.getPapel())) {
            throw new RuntimeException("Acesso negado: Apenas o COORDENADOR possui permissão para alterar o projeto.");
        }

        if (!Boolean.TRUE.equals(vinculo.getAtivo())) {
            throw new RuntimeException("Acesso negado: Seu vínculo com este projeto está inativo.");
        }
    }

    private void validarPermissaoCriacao() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 1. Validação de autenticação básica
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RuntimeException("Acesso negado: É necessário estar logado para realizar esta ação.");
        }

        // 2. O X da questão: Se for ADMIN, ignora o resto e concede permissão imediata
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return; // Passa direto
        }

        // 3. Se não for ADMIN, busca o usuário logado para validar o vínculo
        // institucional
        Usuario logado = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados."));

        // 4. Validação do vínculo: Se não for SERVIDOR (por exemplo, se for ESTUDANTE),
        // o acesso é bloqueado
        // Nota: Certifique-se de que logado.getVinculo() retorna o Enum ou String com o
        // valor "SERVIDOR"
        if (logado.getVinculo() == null || !"SERVIDOR".equalsIgnoreCase(logado.getVinculo().toString())) {
            throw new RuntimeException(
                    "Acesso negado: Apenas Administradores ou Servidores possuem permissão para cadastrar projetos.");
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
        projeto.setVagas(dto.getVagas());
        projeto.setModalidade(dto.getModalidade());
        projeto.setBanner(dto.getBanner());
    }

    public ProjetoResponseDTO toResponseDTO(Projeto projeto) {
        LocalDate hoje = LocalDate.now();
        ProjetoResponseDTO.StatusInscricao status;

        if (hoje.isBefore(projeto.getDataInicioInscricao())) {
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
                .vagas(projeto.getVagas())
                .modalidade(projeto.getModalidade())
                .banner(projeto.getBanner())
                .status(status)
                .build();
    }
}