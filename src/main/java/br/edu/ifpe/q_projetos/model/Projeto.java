package br.edu.ifpe.q_projetos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "projetos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoProjeto tipo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_termino")
    private LocalDate dataTermino;

    @Column(name = "data_inicio_inscricao")
    private LocalDate dataInicioInscricao;

    @Column(name = "data_fim_inscricao")
    private LocalDate dataFimInscricao;

    @Column(name = "link_edital", length = 500)
    private String linkEdital;

    @Column(nullable = false)
    private Integer vagas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModalidadeProjeto modalidade;

    @Column(length = 500)
    private String banner;

    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private StatusModeracao statusModeracao;

    public enum TipoProjeto {
        ENSINO,
        PESQUISA,
        EXTENSAO
    }

    public enum ModalidadeProjeto {
        BOLSISTA,
        VOLUNTARIO,
        AMBOS
    }
    public enum StatusModeracao {
        PENDENTE,
        PUBLICADO,
        REPROVADO
    }
}