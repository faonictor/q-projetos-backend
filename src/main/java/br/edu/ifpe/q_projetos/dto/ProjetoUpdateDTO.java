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
public class ProjetoUpdateDTO {

    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres.")
    private String titulo;

    private TipoProjeto tipo;

    private String descricao;

    private LocalDate dataInicio;

    private LocalDate dataTermino;

    private LocalDate dataInicioInscricao;

    private LocalDate dataFimInscricao;

    @Size(max = 500, message = "O link do edital é muito longo.")
    private String linkEdital;

    @Size(max = 500, message = "O link de inscrição externo é muito longo.")
    private String linkInscricaoExterno;

    @Min(value = 0, message = "A quantidade de vagas não pode ser negativa.")
    private Integer vagas;

    private ModalidadeProjeto modalidade;

    @Size(max = 500, message = "O caminho do banner é muito longo.")
    private String banner;
}