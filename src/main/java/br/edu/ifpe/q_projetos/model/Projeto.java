package br.edu.ifpe.q_projetos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe modelo que representa um Projeto.
 * @Entity: Informa ao Spring Data JPA que esta classe não é um objeto comum, 
 * mas sim uma entidade que deve ser transformada numa tabela no banco de dados.
 */
@Entity
/**
 * @Table: Permite personalizar o nome da tabela no MySQL. 
 * Sem isto, a tabela chamar-se-ia "projeto". O prefixo "tb_" é uma boa prática.
 */
@Table(name = "tb_projetos")
/**
 * @Data: É a "magia" do Lombok. Ele cria invisivelmente todos os métodos 
 * Getters (ex: getNome()), Setters (ex: setNome()), toString(), equals() e hashCode().
 * Isso mantém o ficheiro limpo e fácil de ler.
 */
@Data
/**
 * @NoArgsConstructor: O JPA (ferramenta que gere o banco) obriga que toda a entidade 
 * tenha um construtor vazio. O Lombok cria isso automaticamente.
 */
@NoArgsConstructor
/**
 * @AllArgsConstructor: Cria um construtor que recebe todos os atributos da classe. 
 * Muito útil para testes unitários.
 */
@AllArgsConstructor
public class Projeto {

    /**
     * @Id: Define que este atributo é a Chave Primária (Primary Key) da tabela.
     * É a identidade única do projeto no banco de dados.
     */
    @Id
    /**
     * @GeneratedValue: Define como o ID será gerado. 
     * GenerationType.IDENTITY significa que o próprio MySQL vai assumir a responsabilidade 
     * de criar os IDs de forma sequencial (Auto-Incremento: 1, 2, 3...).
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column: Permite configurar as regras da coluna no banco.
     * nullable = false significa que a coluna "nome" é NOT NULL (obrigatória). 
     * O banco rejeitará o projeto se o nome estiver vazio.
     */
    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String responsavel;

    // Atributos sem anotações específicas viram colunas normais no banco.
    // Inicia como verdadeiro por padrão (um projeto recém-cadastrado está ativo).
    private boolean ativo = true; 

    // --- Campos de Data ---

    /**
     * @CreationTimestamp: Funcionalidade do Hibernate que deteta exatamente 
     * o momento em que o registo é guardado no banco pela primeira vez e 
     * preenche esta data automaticamente.
     * updatable = false: Garante a segurança e integridade do dado. Se alguém 
     * alterar o nome do projeto no futuro, a data de criação não poderá ser sobrescrita.
     */
    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao; // LocalDateTime guarda Dia, Mês, Ano E Hora exata.

    /**
     * @Column(name = "data_inicio"): Força o nome da coluna no banco a usar "snake_case"
     * (separado por underline), o que é o padrão em bancos de dados relacionais.
     */
    @Column(name = "data_inicio")
    private LocalDate dataInicio; // LocalDate guarda apenas Dia, Mês e Ano.
}