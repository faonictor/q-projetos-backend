package br.edu.ifpe.q_projetos.dto;

import br.edu.ifpe.q_projetos.model.Projeto.ModalidadeProjeto;
import br.edu.ifpe.q_projetos.model.Projeto.TipoProjeto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjetoResponseDTO {

    private Long id;
    private String titulo;
    private TipoProjeto tipo;
    private String descricao;
    private LocalDateTime dataCriacao;
    private LocalDate dataInicio;
    private LocalDate dataTermino;
    private LocalDate dataInicioInscricao;
    private LocalDate dataFimInscricao;
    private String linkEdital;
    private Integer vagas;
    private ModalidadeProjeto modalidade;
    private String banner;
    
    // Este campo não existe no banco, mas será calculado no mapeamento
    private StatusInscricao status;

    public enum StatusInscricao {
        ABERTA,
        ENCERRADA,
        AGUARDANDO
    }
}