package br.edu.ifpe.q_projetos.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoDTO {

    @NotNull(message = "O ID do projeto é obrigatório.")
    private Long idProjeto;
}
