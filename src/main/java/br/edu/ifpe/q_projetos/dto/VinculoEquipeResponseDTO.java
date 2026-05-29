package br.edu.ifpe.q_projetos.dto;

import br.edu.ifpe.q_projetos.model.VinculoEquipe.Papel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VinculoEquipeResponseDTO {

    private Long id;
    private Long idProjeto;
    private String tituloProjeto;
    private Long idUsuario;
    private String nomeUsuario;
    private String emailUsuario;
    private Papel papel;
    private Boolean ativo;
}
