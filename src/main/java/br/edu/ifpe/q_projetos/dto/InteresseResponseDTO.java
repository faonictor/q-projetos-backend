package br.edu.ifpe.q_projetos.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InteresseResponseDTO {

    private Long id;
    private Long idProjeto;
    private String nome;
    private String email;
    private String seriePeriodo;
    private String modalidadePretendida;
    private LocalDateTime dataRegistro;
}
