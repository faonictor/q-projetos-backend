package br.edu.ifpe.q_projetos.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InteresseResponseDTO {

    private Long id;
    private Long idProjeto;
    private String tituloProjeto;
    private String nome;
    private String email;
    private String seriePeriodo;
    private String modalidadePretendida;
    private Boolean aceitouLgpd;
    private LocalDateTime dataRegistro;
}
