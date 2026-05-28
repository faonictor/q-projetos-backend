package br.edu.ifpe.q_projetos.DTO;

import br.edu.ifpe.q_projetos.model.VinculoEquipe.Papel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VinculoEquipeDTO {

    @NotNull(message = "O ID do projeto é obrigatório.")
    private Long idProjeto;

    @NotNull(message = "O ID do usuário é obrigatório.")
    private Long idUsuario;

    @NotNull(message = "O papel é obrigatório.")
    private Papel papel;

    @NotNull(message = "O status ativo é obrigatório.")
    private Boolean ativo;
}
