package br.edu.ifpe.q_projetos.dto;

import lombok.Data;

@Data
public class InteresseRequestDTO {

    private Long idProjeto;
    private String nome;
    private String email;
    private String seriePeriodo;
    private String modalidadePretendida;
    private Boolean aceitouLgpd;
}
