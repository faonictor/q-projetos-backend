package br.edu.ifpe.q_projetos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de saída para operações com Favoritos
 * Validação de DTO: Nenhuma entidade Favorito é exposta diretamente
 * Apenas informações necessárias e seguras são retornadas ao cliente
 */
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
