package br.edu.ifpe.q_projetos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpe.q_projetos.dto.FavoritoDTO;
import br.edu.ifpe.q_projetos.dto.FavoritoResponseDTO;
import br.edu.ifpe.q_projetos.exception.RecursoNaoEncontradoException;
import br.edu.ifpe.q_projetos.model.Favorito;
import br.edu.ifpe.q_projetos.model.Projeto;
import br.edu.ifpe.q_projetos.repository.FavoritoRepository;
import br.edu.ifpe.q_projetos.repository.ProjetoRepository;
import br.edu.ifpe.q_projetos.repository.UsuarioRepository;
import br.edu.ifpe.q_projetos.security.SecurityUtils;

/**
 * Service para gerenciar operações com Favoritos
 * 
 * Diretrizes Aplicadas:
 * - Constructor Injection (sem @Autowired)
 * - Validação de DTO: Nenhuma entidade é exposta diretamente
 * - SecurityContextHolder para obter o usuário logado
 * - Todas as operações retornam apenas DTOs (FavoritoResponseDTO)
 */
@Service
public class FavoritoService {

    private final FavoritoRepository repository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor Injection (Padrão mandatório - sem @Autowired)
     * Injeção via construtor garante imutabilidade e facilita testes
     */
    public FavoritoService(FavoritoRepository repository, 
                          ProjetoRepository projetoRepository,
                          UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Vincula um projeto aos favoritos do usuário logado
     * Se o favorito já existe, retorna o existente
     * Se não existe, cria um novo
     * 
     * @param dto DTO contendo o ID do projeto
     * @return FavoritoResponseDTO com dados do favorito criado/atualizado
     */
    @Transactional
    public FavoritoResponseDTO vincularFavorito(FavoritoDTO dto) {
        Long idUsuario = getLoggedUserId();
        
        Favorito favorito = repository.findByIdUsuarioAndIdProjeto(idUsuario, dto.getIdProjeto())
                .orElseGet(() -> repository.save(new Favorito(idUsuario, dto.getIdProjeto())));
        
        return toResponseDTO(favorito);
    }

    /**
     * Remove um projeto dos favoritos do usuário logado
     * Operação idempotente: não lança erro se não existe
     * 
     * @param idProjeto ID do projeto a ser removido dos favoritos
     */
    @Transactional
    public void desvincularFavorito(Long idProjeto) {
        Long idUsuario = getLoggedUserId();
        repository.findByIdUsuarioAndIdProjeto(idUsuario, idProjeto)
                .ifPresent(repository::delete);
    }

    /**
     * Lista todos os favoritos do usuário logado
     * Utiliza SecurityContextHolder para obter o ID do usuário autenticado
     * Retorna apenas os favoritos deste usuário específico
     * 
     * @return Lista de FavoritoResponseDTO do usuário logado
     */
    public List<FavoritoResponseDTO> listarMeuHistorico() {
        Long idUsuario = getLoggedUserId();
        return repository.findByIdUsuario(idUsuario).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os favoritos do sistema
     * Apenas usuários com permissão ADMIN podem acessar
     * Validação de segurança: SecurityUtils.validarPermissaoAdmin()
     * 
     * @return Lista de todos os FavoritoResponseDTO
     * @throws org.springframework.security.access.AccessDeniedException se usuário não for ADMIN
     */
    public List<FavoritoResponseDTO> listarTodos() {
        SecurityUtils.validarPermissaoAdmin();
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um favorito específico por ID
     * Validação: O usuário logado só pode acessar seus próprios favoritos, a menos que seja ADMIN
     * 
     * @param id ID do favorito a ser buscado
     * @return FavoritoResponseDTO com os dados do favorito
     * @throws RecursoNaoEncontradoException se favorito não for encontrado
     * @throws org.springframework.security.access.AccessDeniedException se usuário não tiver permissão
     */
    public FavoritoResponseDTO buscarPorId(Long id) {
        Favorito favorito = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Favorito não encontrado"));

        Long logadoId = getLoggedUserId();
        
        // Valida se o usuário logado é dono do favorito ou é ADMIN
        if (!favorito.getIdUsuario().equals(logadoId)) {
            SecurityUtils.validarPermissaoAdmin();
        }

        return toResponseDTO(favorito);
    }

    /**
     * Obtém o ID do usuário logado a partir do SecurityContextHolder
     * Utiliza a autenticação atual do contexto de segurança do Spring Security
     * Este método é chamado em todas as operações para garantir que o usuário
     * seja identificado corretamente e seus dados sejam isolados
     * 
     * @return ID do usuário autenticado
     * @throws RecursoNaoEncontradoException se o usuário não for encontrado no banco
     */
    private Long getLoggedUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .map(usuario -> usuario.getId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));
    }

    /**
     * Converte entidade Favorito para FavoritoResponseDTO
     * Validação de DTO: Nenhuma entidade Favorito é exposta diretamente
     * Apenas DTOs são retornados aos clientes
     * Protege contra favoritos de projetos deletados retornando mensagem padrão
     * 
     * @param favorito Entidade Favorito a ser convertida
     * @return FavoritoResponseDTO com dados do favorito e informações do projeto
     */
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
