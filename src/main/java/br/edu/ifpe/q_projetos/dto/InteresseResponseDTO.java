package br.edu.ifpe.q_projetos.dto;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class InteresseResponseDTO {

    private Long id;

    private Long projetoId;

    private String tituloProjeto;

    private String nome;

    private String email;

    private String seriePeriodo;

    private String modalidadePretendida;

    private Boolean aceitouLgpd;

    private LocalDateTime dataRegistro;
}
