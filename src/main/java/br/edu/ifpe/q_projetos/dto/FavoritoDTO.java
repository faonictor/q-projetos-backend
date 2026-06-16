package br.edu.ifpe.q_projetos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para operações com Favoritos
 * Validação de DTO: Nenhuma entidade deve ser exposta diretamente
 * Apenas o ID do projeto é necessário para vincular/desvincular favoritos
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoDTO {

    @NotNull(message = "O ID do projeto é obrigatório.")
    @Positive(message = "O ID do projeto deve ser um número positivo.")
    private Long idProjeto;
}