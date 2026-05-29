package br.edu.ifpe.q_projetos.dto;

import br.edu.ifpe.q_projetos.model.Projeto.ModalidadeProjeto;
import br.edu.ifpe.q_projetos.model.Projeto.TipoProjeto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoCreateDTO {

    @NotBlank(message = "O título do projeto é obrigatório.")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres.")
    private String titulo;

    @NotNull(message = "O tipo do projeto (ENSINO, PESQUISA ou EXTENSAO) é obrigatório.")
    private TipoProjeto tipo;

    private String descricao;

    @NotNull(message = "A data de início do projeto é obrigatória.")
    @FutureOrPresent(message = "A data de início não pode ser no passado.")
    private LocalDate dataInicio;

    @NotNull(message = "A data de término do projeto é obrigatória.")
    private LocalDate dataTermino;

    @NotNull(message = "A data de início das inscrições é obrigatória.")
    private LocalDate dataInicioInscricao;

    @NotNull(message = "A data de fim das inscrições é obrigatória.")
    private LocalDate dataFimInscricao;

    @Size(max = 500, message = "O link do edital é muito longo.")
    private String linkEdital;

    @Size(max = 500, message = "O link de inscrição externo é muito longo.")
    private String linkInscricaoExterno;

    @NotNull(message = "A quantidade de vagas é obrigatória.")
    @Min(value = 0, message = "A quantidade de vagas não pode ser negativa.")
    private Integer vagas;

    @NotNull(message = "A modalidade (BOLSISTA, VOLUNTARIO ou AMBOS) é obrigatória.")
    private ModalidadeProjeto modalidade;

    @Size(max = 8000000, message = "O caminho do banner é muito longo.")
    private String banner;

    private Long idCoordenadorManual; // Opcional, usado apenas por Admins
}