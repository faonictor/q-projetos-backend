package br.edu.ifpe.q_projetos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InteresseDTO {

    @NotNull(message = "O ID do projeto é obrigatório.")
    private Long projetoId;

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "A série/período é obrigatória.")
    private String seriePeriodo;

    @NotBlank(message = "A modalidade pretendida é obrigatória.")
    private String modalidadePretendida;

    @NotNull(message = "O aceite da LGPD é obrigatório.")
    private Boolean aceitouLgpd;
}
