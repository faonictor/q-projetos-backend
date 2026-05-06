package br.edu.ifpe.q_projetos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "vinculo_equipe")
public class VinculoEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_projeto", nullable = false)
    private Long idProjeto;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    private String papel;

    @Column(nullable = false)
    private Boolean ativo;
}

//ajuste de karen