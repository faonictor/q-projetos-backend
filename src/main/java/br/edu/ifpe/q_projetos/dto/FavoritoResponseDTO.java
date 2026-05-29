package br.edu.ifpe.q_projetos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritoResponseDTO {

    private Long id;
    private Long idUsuario;
    private Long idProjeto;
    private String tituloProjeto;
    private String bannerProjeto;
    private LocalDateTime dataRegistro;
}
