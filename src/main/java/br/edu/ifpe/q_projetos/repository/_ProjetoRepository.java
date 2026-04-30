package br.edu.ifpe.q_projetos.repository;

import br.edu.ifpe.q_projetos.model._Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface responsável pela comunicação com a tabela de projetos no banco de dados.
 * * @Repository: Diz ao Spring que este arquivo é um componente de acesso a dados.
 * O Spring encarrega-se de o instanciar e gerir invisivelmente.
 */
@Repository
public interface _ProjetoRepository extends JpaRepository<_Projeto, Long> {

    // Não precisamos de escrever NADA aqui dentro para ter o básico a funcionar!
    
    /* * O fato de usarmos "extends JpaRepository<Projeto, Long>" faz com que o Spring
     * nos entregue gratuitamente dezenas de métodos prontos, como:
     * * - save(Projeto p): Salva um novo projeto ou atualiza se já existir.
     * - findAll(): Traz uma lista com todos os projetos cadastrados.
     * - findById(Long id): Procura um projeto específico pelo seu número de ID.
     * - deleteById(Long id): Apaga um projeto do banco de dados.
     * * Onde:
     * <Projeto>: É a classe/entidade que este repositório vai gerir.
     * <Long>: É o tipo de dado da Chave Primária (@Id) dessa classe.
     */
}
