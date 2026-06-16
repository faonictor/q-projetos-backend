package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.dto.FavoritoDTO;
import br.edu.ifpe.q_projetos.dto.FavoritoResponseDTO;
import br.edu.ifpe.q_projetos.service.FavoritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciar operações de Favoritos
 * 
 * Diretrizes Aplicadas:
 * - Constructor Injection (sem @Autowired)
 * - Segurança Declarativa com @PreAuthorize
 * - Validação de DTO em entrada e saída
 * - SecurityContextHolder para usuário logado
 */
@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService service;

    /**
     * Constructor Injection (Padrão mandatório - sem @Autowired)
     * Injeção via construtor garante imutabilidade e facilita testes
     */
    public FavoritoController(FavoritoService service) {
        this.service = service;
    }

    /**
     * Lista todos os favoritos do sistema
     * Acesso restrito apenas para COORD e ADMIN
     * Segurança Declarativa: @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
     * 
     * @return Lista de todos os favoritos (FavoritoResponseDTO)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('COORD', 'ADMIN')")
    public ResponseEntity<List<FavoritoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    /**
     * Retorna o histórico de favoritos do usuário logado
     * Utiliza SecurityContextHolder internamente no service para retornar apenas favoritos do usuário autenticado
     * Acesso: Qualquer usuário autenticado
     * 
     * @return Lista de favoritos do usuário logado (FavoritoResponseDTO)
     */
    @GetMapping("/meu-historico")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FavoritoResponseDTO>> listarMeuHistorico() {
        return ResponseEntity.ok(service.listarMeuHistorico());
    }

    /**
     * Busca um favorito específico por ID
     * O usuário pode acessar apenas seus próprios favoritos, ou se for COORD/ADMIN
     * Validação: A lógica de segurança é validada no Service
     * 
     * @param id ID do favorito a ser buscado
     * @return Detalhes do favorito (FavoritoResponseDTO)
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoritoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    /**
     * Vincula um projeto aos favoritos do usuário logado
     * Validação via DTO: A entidade Favorito nunca é exposta, apenas FavoritoDTO e FavoritoResponseDTO
     * O SecurityContextHolder identifica automaticamente qual usuário está fazendo a requisição
     * 
     * @param dto DTO contendo o ID do projeto a ser favoritado
     * @return Favorito criado/atualizado (FavoritoResponseDTO)
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoritoResponseDTO> salvar(@Valid @RequestBody FavoritoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.vincularFavorito(dto));
    }

    /**
     * Remove um projeto dos favoritos do usuário logado
     * O usuário só pode remover seus próprios favoritos
     * Segurança: Validada automaticamente via SecurityContextHolder no Service
     * 
     * @param idProjeto ID do projeto a ser removido dos favoritos
     */
    @DeleteMapping("/projeto/{idProjeto}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAuthenticated()")
    public void desvincular(@PathVariable Long idProjeto) {
        service.desvincularFavorito(idProjeto);
    }
}