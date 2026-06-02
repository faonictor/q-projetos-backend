package br.edu.ifpe.q_projetos.dto;

import lombok.Data;

@Data
public class InteresseDTO {

    private Long projetoId;

    private String nome;

    private String email;

    private String seriePeriodo;

    private String modalidadePretendida;

    private Boolean aceitouLgpd;
}
