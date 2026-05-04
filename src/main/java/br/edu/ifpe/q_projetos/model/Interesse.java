package br.edu.ifpe.q_projetos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "interesses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interesse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "id_projeto", nullable = false)
    private _Projeto projeto;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(name = "serie_periodo", nullable = false)
    private String seriePeriodo;

    @Column(name = "modalidade_pretendida", nullable = false)
    private String modalidadePretendida;

    @CreationTimestamp
    @Column(name = "data_registro", updatable = false)
    private LocalDateTime dataRegistro;
}
