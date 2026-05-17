package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.DTO.*;
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
    private VinculoEquipeRepository vinculoRepository; // Repositório da outra equipe

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
        // 1. Validar cronograma (Regra de Negócio)
        validarDatas(dto.getDataInicio(), dto.getDataTermino(), dto.getDataInicioInscricao(),
                dto.getDataFimInscricao());

        // 2. Criar a entidade Projeto
        Projeto projeto = new Projeto();
        copiarDadosParaEntidade(dto, projeto);

        // 3. Salvar o Projeto para gerar o ID
        Projeto projetoSalvo = repository.save(projeto);

        // 4. Vincular o Coordenador (ENT05)
        vincularCoordenador(projetoSalvo.getId(), dto.getIdCoordenadorManual());

        return toResponseDTO(projetoSalvo);
    }

    // --- ATUALIZAÇÃO (UPDATE) ---

    @Transactional
    public ProjetoResponseDTO atualizar(Long id, ProjetoUpdateDTO dto) {
        Projeto projeto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        // Validação de Segurança: Somente o coordenador ou ADMIN pode editar (lógica
        // simplificada)
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

        // Atualização de datas com nova validação
        if (dto.getDataInicio() != null)
            projeto.setDataInicio(dto.getDataInicio());
        if (dto.getDataTermino() != null)
            projeto.setDataTermino(dto.getDataTermino());
        // ... repetir para as outras datas se necessário ...

        return toResponseDTO(repository.save(projeto));
    }

    public void deletar(Long id) {
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

        // O Service traduz o Enum do DTO para a query de data correta do banco
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
        
        // 1. Verificamos se existe alguém autenticado e se não é o usuário anônimo do Spring
        // boolean estaAutenticado = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");

        Long idFinalDoCoordenador;

        // Colocando a validação direto no if, a IDE garante que tudo lá dentro está protegido contra NullPointerException
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            
            // Aqui dentro a IDE sabe com 100% de certeza que 'auth' NÃO é nulo
            Usuario logado = usuarioRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco."));

        // if (estaAutenticado) {
        //     // FLUXO REAL: Quando o sistema de login estiver pronto
        //     Usuario logado = usuarioRepository.findByEmail(auth.getName())
        //             .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco."));

            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (idCoordenadorManual != null && isAdmin) {
                idFinalDoCoordenador = idCoordenadorManual;
            } else {
                idFinalDoCoordenador = logado.getId();
            }
        } else {
            // FLUXO DE TESTE (Postman): Se não houver login, ele tenta usar o ID manual que você enviou.
            // Se você não enviar nada, ele usa o ID 1L como padrão (certifique-se de ter um user com ID 1 no banco!)
            idFinalDoCoordenador = (idCoordenadorManual != null) ? idCoordenadorManual : 1L;
            
            System.out.println("⚠️ MODO DE TESTE: Criando vínculo para o usuário ID: " + idFinalDoCoordenador);
        }

        VinculoEquipe vinculo = new VinculoEquipe();
        vinculo.setIdProjeto(projetoId);
        vinculo.setIdUsuario(idFinalDoCoordenador);
        vinculo.setPapel(VinculoEquipe.Papel.COORDENADOR); //estava passando strings, mas precisa ser o ENUM.
        vinculo.setAtivo(true);

        vinculoRepository.save(vinculo);
    }

    private void validarPermissaoEdicao(Long projetoId) {
        // Aqui você implementará a lógica de checar na tabela VinculoEquipe
        // se o usuário logado tem o papel de 'COORDENADOR' para este projetoId.
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