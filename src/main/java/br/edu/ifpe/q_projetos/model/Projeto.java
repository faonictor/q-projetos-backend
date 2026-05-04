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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "projetos")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status_inscricao", nullable = false)
    private StatusInscricao statusInscricao;

    @Column(name = "link_edital")
    private String linkEdital;

    private Integer vagas;

    @Enumerated(EnumType.STRING)
    private ModalidadeProjeto modalidade;

    private String banner;


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

    public enum StatusInscricao {
        ABERTA,
        ENCERRADA,
        AGUARDANDO
    }
}