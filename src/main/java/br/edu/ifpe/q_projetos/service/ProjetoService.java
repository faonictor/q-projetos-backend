package br.edu.ifpe.q_projetos.service;

import br.edu.ifpe.q_projetos.model.Projeto;
import br.edu.ifpe.q_projetos.repository.ProjetoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Service: Esta anotação diz ao Spring Boot que esta classe é um serviço.
 * Ela será responsável por processar a lógica, fazer validações e garantir
 * que as regras do IFPE sejam cumpridas antes de mexer no banco de dados.
 */
@Service
public class ProjetoService {

    /**
     * @Autowired: Injeção de Dependência.
     * Em vez de darmos um "new ProjetoRepository()", o Spring encarrega-se 
     * de criar a ligação com o banco e entregar a ferramenta pronta a usar aqui.
     */
    @Autowired
    private ProjetoRepository repository;

    /**
     * Retorna a lista completa de projetos para alimentar a vitrine do Front-end.
     */
    public List<Projeto> listarTodos() {
        return repository.findAll();
    }

    /**
     * Método para buscar um projeto específico pelo seu ID.
     * Retorna um "Optional", que é a forma segura do Java dizer: 
     * "Pode ser que eu encontre o projeto, pode ser que não (se o ID não existir)".
     */
    public Optional<Projeto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    /**
     * Regra de Negócio principal: Salvar ou Atualizar um projeto.
     */
    public Projeto salvar(Projeto projeto) {
        
        // --- EXEMPLO DE REGRA DE NEGÓCIO ---
        // Se quisermos garantir que o nome do responsável fique sempre em Maiúsculas 
        // para manter o padrão no banco de dados, faríamos isto AQUI, antes de salvar:
        if (projeto.getResponsavel() != null) {
            projeto.setResponsavel(projeto.getResponsavel().toUpperCase());
        }
        
        // Se o projeto for novo, o método .save() cria um novo registo (INSERT).
        // Se o projeto já vier com um ID preenchido no JSON, o .save() atualiza o registo (UPDATE).
        return repository.save(projeto);
    }

    /**
     * Remove um projeto do banco de dados a partir do seu ID.
     */
    public void excluir(Long id) {
        // Exemplo de regra futura: 
        // "Só administradores podem apagar", ou "Projetos antigos não se apagam, apenas se inativam".
        // Essas verificações entrariam aqui antes de chamar o delete.
        repository.deleteById(id);
    }
}