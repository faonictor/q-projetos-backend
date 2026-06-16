package br.edu.ifpe.q_projetos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "vinculo_equipe", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_projeto", "id_usuario"})
})
public class VinculoEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "id_projeto", nullable = false)
    private Long idProjeto;

    @NotNull
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Papel papel;

    @NotNull
    @Column(nullable = false)
    private Boolean ativo;

    public enum Papel {
        COORDENADOR,
        COLABORADOR,
        BOLSISTA,
        VOLUNTARIO
    }
}